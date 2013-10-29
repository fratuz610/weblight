package it.holiday69.weblight.path;

import it.holiday69.weblight.repackaged.StringUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class URIPath
{
  private Logger log = Logger.getAnonymousLogger();

  private List<PathMatcher> _matcherList = new LinkedList();
  private String _pathExpression;

  public URIPath(String pathExpression)
    throws IllegalArgumentException
  {
    _pathExpression = pathExpression;

    if ((pathExpression.indexOf("*") != -1) && !pathExpression.endsWith("*")) {
      throw new IllegalArgumentException("If the path expression contains an *, it must be the last character");
    }
    String[] pathExprList = pathExpression.split("/");

    for (String pathExpr : pathExprList)
    {
      if (!"".equals(pathExpr.trim()))
      {
        if (pathExpr.endsWith("*"))
          _matcherList.add(new StarMatcher());
        else if (pathExpr.startsWith(":"))
          _matcherList.add(new NamedMatcher(pathExpr));
        else
          _matcherList.add(new TextMatcher(pathExpr));
      }
    }
  }

  public Map<String, String> matchAndParse(String actualPath) throws IllegalArgumentException
  {
    String cursorPath = actualPath;
    String actualToken = null;
    List pathMatcherCopyList = new LinkedList(_matcherList);
    Map<String,String> retParamMap = new HashMap<String, String>();
    
    while (true)
    {
      if (StringUtils.isEmpty(cursorPath)) {
        log.finer("Parsing complete");
        break;
      }

      if (cursorPath.indexOf("/") == -1) {
        actualToken = cursorPath;
        cursorPath = "";
      } else {
        actualToken = cursorPath.substring(0, cursorPath.indexOf("/"));
        cursorPath = cursorPath.substring(cursorPath.indexOf("/") + 1);
      }

      log.finer("actualToken: '" + actualToken + "' - remaining string: '" + cursorPath + "'");

      if (!"".equals(actualToken.trim()))
      {
        if (pathMatcherCopyList.isEmpty()) {
          throw new IllegalArgumentException("No matcher for the remaining of the path: " + actualToken + "/" + cursorPath);
        }
        PathMatcher pathMatcher = (PathMatcher)pathMatcherCopyList.remove(0);

        if (!pathMatcher.matches(actualToken)) {
          throw new IllegalArgumentException("'" + actualToken + "' doesn't match " + pathMatcher.getClass().getSimpleName());
        }
        log.finer("Positive match for " + actualToken + " by " + pathMatcher.getClass());

        if (pathMatcher.mustBeLast()) {
          if(!"".equals(cursorPath))
            actualToken = actualToken + "/" + cursorPath;
            
          cursorPath = "";
          log.finer("mustBeLast: actualToken: '" + actualToken + "'");
        }
        if (pathMatcher.getInjectParam(actualToken) != null) {
          retParamMap.put(pathMatcher.getInjectParam(actualToken).getKey(), pathMatcher.getInjectParam(actualToken).getValueAsString());
        }
      }
    }
    if (!pathMatcherCopyList.isEmpty()) {
      throw new IllegalArgumentException("Partial match");
    }
    return retParamMap;
  }
  public String getPathExpression() {
    return _pathExpression;
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (_pathExpression == null ? 0 : _pathExpression.hashCode());

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
    URIPath other = (URIPath)obj;
    if (_pathExpression == null) {
      if (other._pathExpression != null)
        return false;
    } else if (!_pathExpression.equals(other._pathExpression))
      return false;
    return true;
  }
  public String toString() {
    return "[" + getClass().getSimpleName() + " pathExpression: " + _pathExpression + " ]";
  }
}