/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.weblight.router;

import java.util.HashMap;

/**
 *
 * @author fratuz610
 */
public class ValueStack {
  
  private final HashMap<String, Object> _map = new HashMap<String, Object>();
  
  public void set(String key, Object value) {
    
    if(_map.containsKey(key))
      throw new RuntimeException("Key: " + key + " already set");
    
    _map.put(key, value);
  }
  
  public <T> T get(String key, Class<T> classOfT) {
    return classOfT.cast(_map.get(key));
  }
  
}
