package it.holiday69.weblight.path;

import java.util.Arrays;
import java.util.List;

public class StarMatcher
  implements PathMatcher
{
  public boolean matches(String token)
  {
    return true;
  }

  public InjectParam getInjectParam(String value)
  {
    List uriList = Arrays.asList(value.split("/"));
    return new InjectParam("uriList", uriList);
  }

  public boolean mustBeLast() {
    return true;
  }
}