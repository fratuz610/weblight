package it.holiday69.weblight.router;

import com.google.inject.Inject;
import com.google.inject.Injector;
import it.holiday69.weblight.WebLightModule;
import it.holiday69.weblight.anno.WebLight;
import it.holiday69.weblight.model.AttributeNames;
import it.holiday69.weblight.path.URIPath;
import it.holiday69.weblight.repackaged.ExceptionUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RouterServlet extends HttpServlet
{
  private static final long serialVersionUID = 1197202853654328800L;

  @Inject Logger _log;
  @Inject Injector _injector;
  @Inject @WebLight List<WebLightModule.PathBinding> _pathBindingList;
  @Inject AttributeNames _attribNames;
  
  private Map<URIPath, RouterFilterChain> _routingMap = new HashMap();

  @Override
  public void init(ServletConfig config) throws ServletException
  {
    for (WebLightModule.PathBinding pathBinding : _pathBindingList) {
      _log.info("Adding bindingd for " + pathBinding.getPathExpression() + " " + pathBinding.getFilterClassList().size() + " filters and " + pathBinding.getServletClass() + " servlet");
      _routingMap.put(new URIPath(pathBinding.getPathExpression()), new RouterFilterChain(pathBinding.getFilterClassList(), pathBinding.getServletClass(), _injector));
    }
    
    _log.info("Processed: " + _pathBindingList.size() + " path bindings");
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException { handle(req, resp); }
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException { handle(req, resp); } 
  @Override
  public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException { handle(req, resp); }
  @Override
  public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException { handle(req, resp); } 
  @Override
  public void doHead(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException { handle(req, resp); }
  @Override
  public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException { handle(req, resp); }

  private void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
  {
    _log.fine("Got a request for: " + req.getRequestURI());

    for (URIPath uriPath : _routingMap.keySet()) {
      
		  Map<String, String> routeParamMap = new HashMap<String, String>();
		  try {
			  routeParamMap = uriPath.matchAndParse(req.getRequestURI());
		  } catch(IllegalArgumentException ex) {
			  _log.fine("No match for '" + req.getRequestURI() + "' against '"+uriPath+"' => " + ex.getMessage());
			  continue;
		  }
		  
		  for(String routeParamKey : routeParamMap.keySet()) {
        String routeParamValue = routeParamMap.get(routeParamKey);
        
			  _log.fine("Injecting " + routeParamKey + " => " + routeParamValue);
			  req.setAttribute(routeParamKey, routeParamValue);
		  }
      
      req.setAttribute(_attribNames.getRouteParamMapKey(), routeParamMap);
		  
		  RouterFilterChain chain = _routingMap.get(uriPath);
		  
		  _log.fine("Positive match for '"+req.getRequestURI()+"'. Routing through " + chain.toString());
		  
		  try {
			  chain.doFilter(req, resp);
		  } catch(ServletException ex) {
			  _log.warning("Filter exception: " + ExceptionUtils.getDisplableExceptionInfo(ex));
			  resp.setStatus(500);
		  }
		  
		  _log.fine("Request correctly routed");
		  return;
    }
    
    String ignoredURL = (String) req.getAttribute(_attribNames.getForwardedKey());
    
    if(req.getRequestURI().equals(ignoredURL)) {
      _log.fine("The request for '"+req.getRequestURI()+"' didn't match any WebLight route and has already been ignored once, sending 404");
      resp.setStatus(404);
      return;
    }
    
    _log.fine("The request for '"+req.getRequestURI()+"' didn't match any route, ignoring it");
    req.setAttribute(_attribNames.getForwardedKey(), req.getRequestURI());
    
    // we forward the request to the app server
    //req.getRequestDispatcher(req.getRequestURI()).forward(req, resp);
  }
}