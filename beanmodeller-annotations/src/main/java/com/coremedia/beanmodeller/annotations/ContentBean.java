package com.coremedia.beanmodeller.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for content beans. All content beans must be subclasses of AbstractContentBean.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface ContentBean {
  String DOC_TYPE_NAME_USE_CLASS_NAME = "[useClassName]";

  /**
   * The document type name to use in the doctype.xml. This is normally the class name but if it is longer than
   * 18 characters you will get problems, so add a custom name here.
   * @return the name of the document type for the doctypes.xml
   */
  String doctypeName() default DOC_TYPE_NAME_USE_CLASS_NAME;
}
