package it.holiday69.weblight.path;

import java.util.Arrays;
import java.util.List;

public class StarMatcher
  implements PathMatcher
{
  @Override
  public boolean matches(String token)
  {
    return true;
  }

  @Override
  public InjectParam getInjectParam(String value)
  {
    List uriList = Arrays.asList(value.split("/"));
    return new InjectParam("uriList", uriList);
  }

  @Override
  public boolean mustBeLast() {
    return true;
  }
}