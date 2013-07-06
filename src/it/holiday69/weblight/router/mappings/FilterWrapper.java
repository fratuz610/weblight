/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.weblight.router.mappings;

import it.holiday69.weblight.anno.ReqParam;
import it.holiday69.weblight.param.DoubleParam;
import it.holiday69.weblight.param.FloatParam;
import it.holiday69.weblight.param.IntParam;
import it.holiday69.weblight.param.LongParam;
import it.holiday69.weblight.param.Param;
import it.holiday69.weblight.param.StringParam;
import it.holiday69.weblight.router.reply.Reply;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Stefano
 */
public class FilterWrapper implements Filter {
  
  private final static Logger _log = Logger.getLogger(FilterWrapper.class.getSimpleName());
  
  private final static String BEFORE_METHOD_NAME = "before";
  private final static String AFTER_METHOD_NAME = "after";
  
  private Object _filterInstance;
  
  private Method _beforeMethod;
  private Method _afterMethod;
  
  private List<Param> _beforeMethodParamList = new LinkedList<Param>();
  private List<Param> _afterMethodParamList = new LinkedList<Param>();
  
  public FilterWrapper(Class filterClass) {
    
    Method[] methodList = filterClass.getDeclaredMethods();
    
    for(Method method : methodList) {
      
      if(BEFORE_METHOD_NAME.equals(method.getName())) {
        
        if(_beforeMethod != null)
          throw new RuntimeException("Multiple 'before' methods founds, only one is allowed");
        
        if(method.getReturnType() != Reply.class)
          throw new RuntimeException("Before method should return an 'Reply' object");
        
        _beforeMethod = method;
        _beforeMethodParamList = getParamList(method);
        
      }
      
      if(AFTER_METHOD_NAME.equals(method.getName())) {
        
        if(_afterMethod != null)
          throw new RuntimeException("Multiple 'after' methods founds, only one is allowed");
        
        if(method.getReturnType() != Reply.class)
          throw new RuntimeException("'After' method should return an 'Reply' object");
        
        _afterMethod = method;
        _afterMethodParamList = getParamList(method);
        
      }
      
      if(_beforeMethod == null)
        throw new RuntimeException("No 'before' method specified in this filter!");
      
      try {
        _filterInstance = filterClass.newInstance();
      } catch (Throwable th) {
        throw new RuntimeException("Unable to instanciate filter class " + filterClass, th);
      }
        
    }
    
  }
  
  private List<Param> getParamList(Method method) {
    
    Annotation[][] annotationList = method.getParameterAnnotations();
    Class<?>[] paramClassList = method.getParameterTypes();

    List<Param> retList = new LinkedList<Param>();
    
    int cnt = 0;
    for(Annotation[] paramAnnotationList : annotationList) {
      if(paramAnnotationList.length == 0) {
        retList.add(cnt, null);
        continue;
      }

      Class<?> paramClass = paramClassList[cnt];

      for(Annotation annotation : paramAnnotationList) {

        if(annotation instanceof ReqParam) {

          ReqParam paramAnnotation = (ReqParam) annotation;

          _log.info("Find a param annotation with linked class: " + paramClass + " and value: " + paramAnnotation.value());

          Param param;

          if(paramClass == Double.class) param = new DoubleParam(paramAnnotation.value());
          else if(paramClass == Float.class) param = new FloatParam(paramAnnotation.value());
          else if(paramClass == Integer.class) param = new IntParam(paramAnnotation.value());
          else if(paramClass == LongParam.class) param = new LongParam(paramAnnotation.value());
          else if(paramClass == String.class) param = new StringParam(paramAnnotation.value());
          else
            throw new RuntimeException("Parameter of class " + paramClass + " is not supported at the moment");

          retList.add(cnt, param);

        }

      }
    }
    
    return retList;
  }
  
  @Override
  public void doFilter(ServletRequest srcRequest, ServletResponse srcResponse, FilterChain chain) throws IOException, ServletException {
    
    HttpServletRequest request = (HttpServletRequest) srcRequest;
    HttpServletResponse response = (HttpServletResponse) srcResponse;
    
    Reply<?> replyObj = invokeFilterMethod(_beforeMethod, _beforeMethodParamList, request);
    
    if(replyObj != null) {
      
      replyObj.render(response);

      return;
    }
    
    // we trigger the filter chain
    chain.doFilter(srcRequest, srcResponse);
    
    // we trigger the after method if any
    if(_afterMethod != null) {
      replyObj = invokeFilterMethod(_afterMethod, _afterMethodParamList, request);
      
      if(replyObj != null)
        replyObj.render(response);
    }
    
  }
  
  private Reply<?> invokeFilterMethod(Method method, List<Param> paramList, HttpServletRequest request) {
    
    List<Object> actualParamList = new LinkedList<Object>();

    for(Param param : paramList) {

      if(param == null)
        actualParamList.add(null);

      String paramName = param.getParamName();

      String paramValue = request.getParameter(paramName);

      Object value = null;
      if(paramValue != null) {
        try {
          value = param.parse(paramValue);
        } catch(Throwable th) {
          value = null;
        }  
      }
      actualParamList.add(value);

    }
    
    try {
      
      return (Reply<?>) method.invoke(_filterInstance, actualParamList.toArray(new Object[actualParamList.size()]));
      
    } catch(Throwable th) {
      throw new RuntimeException("Unable to invoke filter method: " + _beforeMethod.getName(), th);
    }
    
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    
  }

  @Override
  public void destroy() {
    
  }
  
  public static class AuthFilter {
    
    public Reply<?> before(@ReqParam("username") String username, String password) {
		
      return null; // continues execution

    }
    
  }
  
  public static void main(String[] args) {
    
    FilterWrapper mapping = new FilterWrapper(AuthFilter.class);
    
  }
  
}
