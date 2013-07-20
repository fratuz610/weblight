package it.holiday69.weblight.router;

import com.google.inject.Injector;
import it.holiday69.weblight.anno.Header;
import it.holiday69.weblight.anno.ReqParam;
import it.holiday69.weblight.anno.RouteParam;
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

  public RouterFilterChain(Set<Class<? extends Filter>> filterClassList, Class<? extends HttpServlet> servletClass, Injector injector)
  {
    _filterClassList = filterClassList;
    _servletClass = servletClass;
    _injector = injector;
  }

  @Override
  public void doFilter(ServletRequest servReq, ServletResponse servResp) throws IOException, ServletException
  {
    
    // we get the servlet instance from the request
    HttpServlet servlet = (HttpServlet) servReq.getAttribute("__servletInstance__");
    
    // if none we istanciate and inject values
    if(servlet == null) {
      servlet = (HttpServlet)_injector.getInstance(_servletClass);
      
      // we inject headers and parameters
      scanAndInject(servlet, (HttpServletRequest) servReq);
      
      servReq.setAttribute("__servletInstance__", servlet);
    }
    
    // we get the list of filters from the request
    List<Filter> workingFilterList = (List<Filter>)servReq.getAttribute("__workingFilterList__");
    
    if (workingFilterList == null) {
      
      workingFilterList = new LinkedList<Filter>();
      
      for (Class filterClass : _filterClassList) {
        Filter filter = (Filter)_injector.getInstance(filterClass);
        
        // we inject other parameters (headers and params)
        scanAndInject(filter, (HttpServletRequest) servReq);
        
        if (filter == null)
          throw new RuntimeException("Unable to get an instance for class " + filterClass);
        
        workingFilterList.add(filter);
      }
      
      servReq.setAttribute("__workingFilterList__", workingFilterList);
    }
    
    if (!workingFilterList.isEmpty()) {
      _log.fine("Working filter has: " + workingFilterList.size() + " elements");

      Filter filter = (Filter)workingFilterList.remove(0);
      servReq.setAttribute("__workingFilterList__", workingFilterList);

      _log.fine("Applying filter: " + filter.getClass());
      filter.doFilter(servReq, servResp, this);
    } else {
      _log.fine("No more filters to process, serving the servlet");
      servReq.setAttribute("__workingFilterList__", null);
      servlet.service(servReq, servResp);
    }
  }
  
  private void scanAndInject(Object obj, HttpServletRequest req) {
    
    Class<?> clazz = obj.getClass();
    
    Map<String, String> routeParamMap = (Map<String, String>) req.getAttribute("__routeParamMap__");
      
    for(Field field : clazz.getDeclaredFields()) {
      
      // allow access to private fields
      field.setAccessible(true); 
      
      // headers
      if(field.isAnnotationPresent(Header.class)) {

        if(field.getType() != String.class)
          throw new RuntimeException("Field " + field.getName() + " is annotated with @Header but it's not a String: " + field.getType());

        Header headerAnno = field.getAnnotation(Header.class);
        
        String headerValue = req.getHeader(headerAnno.value());
        
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
        
        String paramName = reqParamAnno.value() != null?reqParamAnno.value():field.getName();
        
        String paramValue = req.getParameter(paramName);
        
        // if we have a param with matching name
        if(paramValue != null) {
          
          try {
            if(field.getType() == int.class || field.getType() == Integer.class) {
              field.set(obj, Integer.parseInt(paramValue));
            } else if(field.getType() == String.class) {
              field.set(obj, paramValue);
            } else if(field.getType() == long.class || field.getType() == Long.class) {
              field.set(obj, Long.parseLong(paramValue));
            } else if(field.getType() == float.class || field.getType() == Float.class) {
              field.set(obj, Float.parseFloat(paramValue));
            } else if(field.getType() == double.class || field.getType() == Double.class) {
              field.set(obj, Double.parseDouble(paramValue));
            } else if(field.getType() == boolean.class || field.getType() == Boolean.class) {
              field.set(obj, Boolean.parseBoolean(paramName));
            }
          } catch(Throwable th) { 
            // smth went wrong
          }
          
        }
        
      }
      
      // route parameters
      if(field.isAnnotationPresent(RouteParam.class)) {
        
        RouteParam reqParamAnno = field.getAnnotation(RouteParam.class);
        
        String paramName = reqParamAnno.value() != null?reqParamAnno.value():field.getName();
        
        String paramValue = routeParamMap.get(paramName);
        
        // if we have a param with matching name
        if(paramValue != null) {
          
          try {
            if(field.getType() == int.class || field.getType() == Integer.class) {
              field.set(obj, Integer.parseInt(paramValue));
            } else if(field.getType() == String.class) {
              field.set(obj, paramValue);
            } else if(field.getType() == long.class || field.getType() == Long.class) {
              field.set(obj, Long.parseLong(paramValue));
            } else if(field.getType() == float.class || field.getType() == Float.class) {
              field.set(obj, Float.parseFloat(paramValue));
            } else if(field.getType() == double.class || field.getType() == Double.class) {
              field.set(obj, Double.parseDouble(paramValue));
            } else if(field.getType() == boolean.class || field.getType() == Boolean.class) {
              field.set(obj, Boolean.parseBoolean(paramName));
            }
          } catch(Throwable th) { 
            // smth went wrong
          }
          
        }
        
      }
      
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