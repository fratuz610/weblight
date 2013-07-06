package it.holiday69.weblight.router.reply;

import it.holiday69.weblight.router.inte.Template;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class Reply<T>
{
  private T _replyObj;
  private Template _templ;
  private int _status = 200;
  private String _redirectUrl = null;

  public Reply<T> with(T replyObj)
  {
    _replyObj = replyObj;
    return this;
  }

  public Reply<T> as(Class<? extends Template> templateClass) {
    try {
      _templ = ((Template)templateClass.newInstance());
    } catch (Throwable th) {
      throw new RuntimeException(th);
    }
    return this;
  }

  public void status(int status) {
    _status = status;
  }

  public void notFound() {
    _status = 404;
  }

  public void seeOther(String url) {
    _status = 302;
    _redirectUrl = url;
  }

  public void unauthorized() {
    _status = 403;
  }

  public void internalError() {
    _status = 500;
  }

  public void accepted() {
    _status = 202;
  }

  public void render(HttpServletResponse resp)
  {
    if ((_status == 302) && (_redirectUrl == null)) {
      throw new RuntimeException("Unable to send a redirect, no redirect url set!");
    }
    if ((_replyObj == null) && (_templ == null))
      _templ = new TextTemplate();
    try
    {
      if (_status == 302) {
        resp.sendRedirect(_redirectUrl);
      } else if (_status > 399) {
        resp.sendError(_status);
      } else {
        resp.setStatus(_status);
        if (_replyObj != null)
          resp.getOutputStream().write(_templ.renderTemplate(_replyObj).getBytes());
      }
    } catch (IOException ex) {
    }
  }

  public int getStatus() {
    return _status;
  }
}