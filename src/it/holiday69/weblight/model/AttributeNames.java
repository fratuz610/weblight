/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.weblight.model;

/**
 *
 * @author fratuz610
 */
public class AttributeNames {
  
  private final String _forwardedKey = "__weblight-forwarded__";
  private final String _servletInstanceKey = "__servletInstance__";
  private final String _workingFilterListKey = "__workingFilterList__";
  private final String _routeParamListKey = "__routeParamMap__";
  
  public String getForwardedKey() { return _forwardedKey; }
  public String getServletInstanceKey() { return _servletInstanceKey; }
  public String getWorkingFilterListKey() { return _workingFilterListKey; }
  public String getRouteParamMapKey() { return _routeParamListKey; }
}
