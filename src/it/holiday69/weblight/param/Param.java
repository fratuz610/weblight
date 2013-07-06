package it.holiday69.weblight.param;

public abstract interface Param<T>
{
  public abstract T parse(String paramString);

  public abstract String getParamName();
}