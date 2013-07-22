package it.holiday69.weblight.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute
{
  public abstract String value() default "";
  public abstract boolean mandatory() default true;
}