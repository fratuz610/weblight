package it.holiday69.weblight;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import it.holiday69.weblight.anno.WebLight;
import it.holiday69.weblight.model.AttributeNames;
import it.holiday69.weblight.router.RouterFilter;
import it.holiday69.weblight.router.RouterServlet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

public abstract class WebLightModule extends AbstractModule
{
  private final List<PathBinding> _bindings;

  public WebLightModule()
  {
    _bindings = new LinkedList();
  }

  @Override
  protected void configure()
  {
    bind(new TypeLiteral<List<PathBinding>>(){}).annotatedWith(WebLight.class).toInstance(_bindings);
    
    configureWebLight();

    install(new ServletModule()
    {
      @Override
      protected void configureServlets() {
        bind(RouterServlet.class).in(Singleton.class);
        bind(RouterFilter.class).in(Singleton.class);
        bind(AttributeNames.class).in(Singleton.class);
        
        filterRegex("/.*").through(RouterFilter.class);
        //serveRegex("^(/[^_]).*").with(RouterServlet.class);
        //serveRegex("/.*").with(RouterServlet.class);
      }
    });
  }

  protected abstract void configureWebLight();

  protected final PathBinding at(String pathExpression) {
    return new PathBinding(pathExpression);
  }

  public class PathBinding
  {
    private String _pathExpression;
    private final Set<Class<? extends Filter>> _filterClassList = new LinkedHashSet();
    private Class<? extends HttpServlet> _servletClass = null;

    public PathBinding(String pathExpression) {
      _pathExpression = pathExpression;
    }

    public PathBinding filter(Class<? extends Filter> filterClass) {
      _filterClassList.add(filterClass);
      return this;
    }

    public void serveWith(Class<? extends HttpServlet> servletClass) {
      _servletClass = servletClass;
      _bindings.add(this);
    }

    public String getPathExpression() { return _pathExpression; } 
    public Set<Class<? extends Filter>> getFilterClassList() { return _filterClassList; } 
    public Class<? extends HttpServlet> getServletClass() { return _servletClass; }

  }
}