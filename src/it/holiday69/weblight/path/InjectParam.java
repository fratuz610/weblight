package it.holiday69.weblight.path;

public class InjectParam
{
  private String _key;
  private Object _value;

  public InjectParam(String key, Object value)
  {
    _key = key;
    _value = value;
  }
  public String getKey() {
    return _key; } 
  public Object getValue() { return _value; }

}