package it.holiday69.weblight.param;

public class LongParam
  implements Param<Long>
{
  private String _name;

  public LongParam(String name)
  {
    _name = name;
  }

  public Long parse(String source)
  {
    return Long.valueOf(source);
  }

  public String getParamName()
  {
    return _name;
  }
}