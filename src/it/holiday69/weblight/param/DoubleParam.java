package it.holiday69.weblight.param;

public class DoubleParam
  implements Param<Double>
{
  private String _name;

  public DoubleParam(String name)
  {
    _name = name;
  }

  public Double parse(String source)
  {
    return Double.valueOf(source);
  }

  public String getParamName()
  {
    return _name;
  }
}