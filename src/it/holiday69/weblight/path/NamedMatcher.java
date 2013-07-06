package it.holiday69.weblight.path;

public class NamedMatcher
  implements PathMatcher
{
  private String _variableName;

  public NamedMatcher(String namedToken)
  {
    if (!namedToken.startsWith(":")) {
      throw new IllegalArgumentException("Named token: " + namedToken + " should start with ':'");
    }
    _variableName = namedToken.substring(1);
  }

  public boolean matches(String token)
  {
    return true;
  }

  public InjectParam getInjectParam(String value)
  {
    return new InjectParam(_variableName, value);
  }

  public boolean mustBeLast() {
    return false;
  }

  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = 31 * result + (_variableName == null ? 0 : _variableName.hashCode());

    return result;
  }

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NamedMatcher other = (NamedMatcher)obj;
    if (_variableName == null) {
      if (other._variableName != null)
        return false;
    } else if (!_variableName.equals(other._variableName))
      return false;
    return true;
  }
}