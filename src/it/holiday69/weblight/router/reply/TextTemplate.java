package it.holiday69.weblight.router.reply;

import it.holiday69.weblight.router.inte.Template;

public class TextTemplate implements Template
{
  @Override
  public String renderTemplate(Object obj)
  {
    return obj.toString();
  }
}