package it.holiday69.weblight.router;

import com.google.gson.Gson;
import com.google.inject.Injector;
import it.holiday69.weblight.anno.Header;
import it.holiday69.weblight.anno.ReqParam;
import it.holiday69.weblight.anno.RouteParam;
import it.holiday69.weblight.anno.Attribute;
import it.holiday69.weblight.anno.RawInput;
import it.holiday69.weblight.anno.RouteStarParam;
import it.holiday69.weblight.model.AttributeNames;
import it.holiday69.weblight.repackaged.StringUtils;
import it.holiday69.weblight.utils.IOHelper;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public class RouterFilterChain implements FilterChain
{
  
  private Logger _log = Logger.getLogger(RouterFilterChain.class.getSimpleName());
  private Set<Class<? extends Filter>> _filterClassList;
  private Class<? extends HttpServlet> _servletClass;
  private Injector _injector;
  private AttributeNames _attributeNames;

  public RouterFilterChain(Set<Class<? extends Filter>> filterClassList, Class<? extends HttpServlet> servletClass, Injector injector)
  {
    _filterClassList = filterClassList;
    _servletClass = servletClass;
    _injector = injector;
    _attributeNames = injector.getInstance(AttributeNames.class);
  }

  @Override
  public void doFilter(ServletRequest servReq, ServletResponse servResp) throws IOException, ServletException
  {
   
    // we get the servlet instance from the request
    HttpServlet servlet = (HttpServlet) servReq.getAttribute(_attributeNames.getServletInstanceKey());
    
    // if none we istanciate and inject values
    if(servlet == null) {
      servlet = (HttpServlet)_injector.getInstance(_servletClass);
      
      // we inject headers and parameters
      injectRequestParams(servlet, (HttpServletRequest) servReq);
      
      servReq.setAttribute(_attributeNames.getServletInstanceKey(), servlet);
    }
    
    // we get the list of filters from the request
    List<Filter> workingFilterList = (List<Filter>)servReq.getAttribute(_attributeNames.getWorkingFilterListKey());
    
    if (workingFilterList == null) {
      
      workingFilterList = new LinkedList<Filter>();
      
      for (Class filterClass : _filterClassList) {
        Filter filter = (Filter)_injector.getInstance(filterClass);
        
        // we inject other parameters (headers and params)
        injectRequestParams(filter, (HttpServletRequest) servReq);
        
        if (filter == null)
          throw new RuntimeException("Unable to get an instance for class " + filterClass);
        
        workingFilterList.add(filter);
      }
      
      servReq.setAttribute(_attributeNames.getWorkingFilterListKey(), workingFilterList);
    }
    
    if (!workingFilterList.isEmpty()) {
      _log.fine("Working filter has: " + workingFilterList.size() + " elements");

      Filter filter = (Filter)workingFilterList.remove(0);
      servReq.setAttribute(_attributeNames.getWorkingFilterListKey(), workingFilterList);
      
      // updates the attributes
      injectAttributes(filter, (HttpServletRequest) servReq);
      
      _log.fine("Applying filter: " + filter.getClass());
      filter.doFilter(servReq, servResp, this);
    } else {
      _log.fine("No more filters to process, serving the servlet");
      servReq.setAttribute(_attributeNames.getWorkingFilterListKey(), null);
      
      // updates the attributes
      injectAttributes(servlet, (HttpServletRequest) servReq);
      
      servlet.service(servReq, servResp);
    }
  }
  
  private void injectAttributes(Object obj, HttpServletRequest req) {
    
    Class<?> clazz = obj.getClass();
      
    for(Field field : clazz.getDeclaredFields()) {
      
      // allow access to private fields
      field.setAccessible(true); 
      
      // Attributes
      if(field.isAnnotationPresent(Attribute.class)) {
        
        Attribute attribAnno = field.getAnnotation(Attribute.class);
        
        String attribKey = StringUtils.hasContent(attribAnno.value())?attribAnno.value():field.getName();
        
        Object attribValue = req.getAttribute(attribKey);
        
        if(attribValue == null)
          continue;
        
        try {
          field.set(obj, attribValue);
        } catch(Throwable th) {
          if(!field.getType().isAssignableFrom(attribValue.getClass()))
            throw new RuntimeException("Field " + field.getName() + " of type " + field.getType() + "  is not assignable from " + attribValue.getClass());
          else
            throw new RuntimeException(th);
        }
      }
    }
  }
    
  private void injectRequestParams(Object obj, HttpServletRequest req) {
    
    Class<?> clazz = obj.getClass();
    
    Map<String, String> routeParamMap = (Map<String, String>) req.getAttribute(_attributeNames.getRouteParamMapKey());
      
    for(Field field : clazz.getDeclaredFields()) {
      
      // allow access to private fields
      field.setAccessible(true); 
      
      // headers
      if(field.isAnnotationPresent(Header.class)) {

        if(field.getType() != String.class)
          throw new RuntimeException("Only String fields can be annotated with @Header. '" + field.getName() + "' is annotated with @Header but it's an instance of " + field.getType());

        Header headerAnno = field.getAnnotation(Header.class);
        
        String headerName = StringUtils.hasContent(headerAnno.value())?headerAnno.value():field.getName();
        
        String headerValue = req.getHeader(headerName);
        
        if(headerValue != null) {
          
          _log.fine("Injecting header: '" + headerAnno.value() + "' into field '" + field.getName() + "' with value: '" + headerValue + "'");
          try {
            field.set(obj, headerValue);
          } catch(Throwable th) {
            throw new RuntimeException(th);
          }
        }
      }
      
      // request parameters
      if(field.isAnnotationPresent(ReqParam.class)) {
        
        ReqParam reqParamAnno = field.getAnnotation(ReqParam.class);
        
        String paramName = StringUtils.hasContent(reqParamAnno.value())?reqParamAnno.value():field.getName();
        
        String paramValue = req.getParameter(paramName);
        
        // if we have a param with matching name
        if(paramValue != null)
          assignField(obj, field, paramValue);
        
      }
      
      // route parameters
      if(field.isAnnotationPresent(RouteParam.class)) {
        
        RouteParam routeParamAnno = field.getAnnotation(RouteParam.class);
        
        String paramName = StringUtils.hasContent(routeParamAnno.value())?routeParamAnno.value():field.getName();
        
        String paramValue = routeParamMap.get(paramName);
        
        // if we have a param with matching name
        if(paramValue != null)
          assignField(obj, field, paramValue);
        
      }
      
      // route star param
      if(field.isAnnotationPresent(RouteStarParam.class)) {
        
        String paramValue = routeParamMap.get("__route_star__");
        
        // if we have a param with matching name
        if(paramValue != null)
          assignField(obj, field, paramValue);
      }
      
      
      // raw request parameter
      if(field.isAnnotationPresent(RawInput.class)) {
        
        RawInput rawInputAnno = field.getAnnotation(RawInput.class);
        
        if(StringUtils.isEmpty(rawInputAnno.value())) {
          
          // no input type specified, defaulting to byte array or string
          if(field.getType() != byte[].class && field.getType() != String.class)
            throw new RuntimeException("Fields annotated with @RawInput and no input type must be either of type byte[] or String. Found: '" + field.getType() + "'");

          try {
            if(field.getType() == byte[].class)
              field.set(obj, new IOHelper().readAsByteArray(req.getInputStream()));
            else if(field.getType() == String.class)
              field.set(obj, new IOHelper().readAsString(req.getInputStream()));
          } catch(Throwable th) {
            // smth went wrong
          }        
        } else if("json".equalsIgnoreCase(rawInputAnno.value())) {
          
          try {
            field.set(obj, new Gson().fromJson(new IOHelper().readAsString(req.getInputStream()), field.getType()));
          } catch(Throwable th) {
            // smth went wrong
          } 
        }
      }
      
    }
    
  }
  
  private void assignField(Object obj, Field field, String stringValue ){
    try {
      if(field.getType() == int.class || field.getType() == Integer.class) {
        field.set(obj, Integer.parseInt(stringValue));
      } else if(field.getType() == String.class) {
        field.set(obj, stringValue);
      } else if(field.getType() == long.class || field.getType() == Long.class) {
        field.set(obj, Long.parseLong(stringValue));
      } else if(field.getType() == float.class || field.getType() == Float.class) {
        field.set(obj, Float.parseFloat(stringValue));
      } else if(field.getType() == double.class || field.getType() == Double.class) {
        field.set(obj, Double.parseDouble(stringValue));
      } else if(field.getType() == boolean.class || field.getType() == Boolean.class) {
        field.set(obj, Boolean.parseBoolean(stringValue));
      }
    } catch(Throwable th) { 
      // smth went wrong
    }
  }

  @Override
  public String toString()
  {
    String ret = "[RouterFilterChain filterList: ";
    for (Class filterClass : _filterClassList)
      ret = ret + filterClass.getSimpleName() + " ";
    
    if (_servletClass != null)
      return ret + "servlet: " + _servletClass.getSimpleName() + " ]";
    
    return ret + "servlet: null ]";
  }
}