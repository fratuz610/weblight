package it.holiday69.weblight.path;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class IntRouteParam
{
  private String _key;
  private Object _value;

  public IntRouteParam(String key, Object value)
  {
    _key = key;
    _value = value;
  }
  public String getKey() {
    return _key; } 
  public Object getValue() { return _value; }
  public String getValueAsString() { 
    try {
      return URLDecoder.decode((String) _value, "UTF-8");
    } catch(UnsupportedEncodingException ex) {
      throw new RuntimeException("Error URL decoding route param '" + _key + "' because " + ex.getMessage(), ex);
    }
  }

}