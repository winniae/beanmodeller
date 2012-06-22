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
  String DOC_TYPE_ASPECT_DISABLED ="[notAnDoctypeAspect]";

  /**
   * The document type name to use in the doctype.xml. This is normally the class name but if it is longer than
   * 18 characters you will get problems, so add a custom name here.
   *
   * @return the name of the document type for the doctypes.xml
   */
  String doctypeName() default DOC_TYPE_NAME_USE_CLASS_NAME;

  /**
   * Is this Content meant to be abstract - by default false.
   *
   * @return if this content bean is meant to be an abstract document type
   */
  boolean isAbstract() default false;

  /**
   * Instead of creating a parent doctype, take the given doctype and extend it
   * via CoreMedia DocTypeAspect.
   *
   * This allows you to insert additional properties for existing doctypes, e.g. from
   * CoreMedia Blueprint but use your own ContentBean.
   *
   * @return Name of doctype to generate aspect for or empty String if not set.
   */
  String aspectDoctypeName() default DOC_TYPE_ASPECT_DISABLED;
}
