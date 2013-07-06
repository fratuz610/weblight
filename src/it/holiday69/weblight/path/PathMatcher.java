package it.holiday69.weblight.path;

public abstract interface PathMatcher
{
  public abstract boolean matches(String paramString);

  public abstract InjectParam getInjectParam(String paramString);

  public abstract boolean mustBeLast();
}