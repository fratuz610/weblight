package it.holiday69.weblight.param;

public class IntParam
  implements Param<Integer>
{
  private String _name;

  public IntParam(String name)
  {
    _name = name;
  }

  public Integer parse(String source)
  {
    return Integer.valueOf(source);
  }

  public String getParamName()
  {
    return _name;
  }
}