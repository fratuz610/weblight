package it.holiday69.weblight.path;

public class TextMatcher
  implements PathMatcher
{
  private String _token;

  public TextMatcher(String token)
  {
    _token = token;
  }

  public boolean matches(String token)
  {
    if (_token.equalsIgnoreCase(token)) {
      return true;
    }
    return false;
  }

  public boolean mustBeLast() {
    return false;
  }

  public IntRouteParam getInjectParam(String value) {
    return null;
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (_token == null ? 0 : _token.hashCode());
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
    TextMatcher other = (TextMatcher)obj;
    if (_token == null) {
      if (other._token != null)
        return false;
    } else if (!_token.equals(other._token))
      return false;
    return true;
  }
}