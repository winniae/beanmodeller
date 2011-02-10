package com.coremedia.beanmodeller.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface ContentBean {
  String DOC_TYPE_NAME_USE_CLASS_NAME = "[useClassName]";

  String doctypeName() default DOC_TYPE_NAME_USE_CLASS_NAME;
}
