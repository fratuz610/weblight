package it.holiday69.weblight.router;

import com.google.inject.Injector;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

public class RouterFilterChain
  implements FilterChain
{
  private Logger _log = Logger.getLogger(RouterFilterChain.class.getSimpleName());
  private Set<Class<? extends Filter>> _filterClassList;
  private Class<? extends HttpServlet> _servletClass;
  private List<Filter> _filterInstanceList = new LinkedList();
  private HttpServlet _servletInstance = null;
  private Injector _injector;

  public RouterFilterChain(Set<Class<? extends Filter>> filterClassList, Class<? extends HttpServlet> servletClass, Injector injector)
  {
    _filterClassList = filterClassList;
    _servletClass = servletClass;
    _injector = injector;
  }

  public void doFilter(ServletRequest servReq, ServletResponse servResp)
    throws IOException, ServletException
  {
    if (_servletClass == null) {
      throw new ServletException("No servlet set for this RouterFilterChain");
    }
    if (_filterInstanceList.isEmpty()) {
      for (Class filterClass : _filterClassList) {
        Filter filter = (Filter)_injector.getInstance(filterClass);
        if (filter == null) {
          throw new RuntimeException("Unable to get an instance for class " + filterClass);
        }
        _filterInstanceList.add(filter);
      }

    }

    if (_servletInstance == null) {
      _servletInstance = ((HttpServlet)_injector.getInstance(_servletClass));
    }
    List workingFilterList = (List)servReq.getAttribute("__workingFilterList__");

    if (workingFilterList == null) {
      workingFilterList = new LinkedList(_filterInstanceList);
    }

    if (!workingFilterList.isEmpty())
    {
      _log.fine("Working filter has: " + workingFilterList.size() + " elements");

      Filter filter = (Filter)workingFilterList.remove(0);
      servReq.setAttribute("__workingFilterList__", workingFilterList);

      _log.fine("Applying filter: " + filter.getClass());
      filter.doFilter(servReq, servResp, this);
    }
    else
    {
      _log.fine("No more filters to process, serving the servlet");
      servReq.setAttribute("__workingFilterList__", null);
      _servletInstance.service(servReq, servResp);
    }
  }

  public String toString()
  {
    String ret = "[RouterFilterChain filterList: ";
    for (Class filterClass : _filterClassList)
      ret = ret + filterClass.getSimpleName() + " ";
    if (_servletClass != null) {
      return ret + "servlet: " + _servletClass.getSimpleName() + " ]";
    }
    return ret + "servlet: null ]";
  }
}