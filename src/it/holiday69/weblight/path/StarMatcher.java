package it.holiday69.weblight.path;

public class StarMatcher implements PathMatcher
{
  
  @Override
  public boolean matches(String token)
  {
    return true;
  }

  @Override
  public IntRouteParam getInjectParam(String value)
  {
    return new IntRouteParam("__route_star__", value);
  }

  @Override
  public boolean mustBeLast() {
    return true;
  }
}