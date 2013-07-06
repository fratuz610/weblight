package it.holiday69.weblight.param;

public class FloatParam
  implements Param<Float>
{
  private String _name;

  public FloatParam(String name)
  {
    _name = name;
  }

  public Float parse(String source)
  {
    return Float.valueOf(source);
  }

  public String getParamName()
  {
    return _name;
  }
}