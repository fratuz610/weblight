package it.holiday69.weblight.router.reply;

import com.google.gson.Gson;
import it.holiday69.weblight.router.inte.Template;

public class JsonTemplate implements Template
{
  @Override
  public String renderTemplate(Object obj)
  {
    return new Gson().toJson(obj);
  }
}