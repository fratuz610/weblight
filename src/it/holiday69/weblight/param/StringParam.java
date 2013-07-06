package it.holiday69.weblight.param;

public class StringParam
  implements Param<String>
{
  private String _name;

  public StringParam(String name)
  {
    _name = name;
  }

  public String parse(String source)
  {
    return source;
  }

  public String getParamName()
  {
    return _name;
  }
}