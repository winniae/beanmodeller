package com.coremedia.beanmodeller.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A marker for content bean properties. Normaly you do not need to mark the method since ll abstract methods
 * returning a 'compatible' object will be converted to content bean properties.
 * This annotations allws to tweak the properties.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface ContentProperty {
  String PROPERTY_NAME_USE_METHOD_NAME = "[useMethodName]";
  int STRING_PROPERTY_DEFAULT_LENGTH = -1;
  String MARKUP_PROPERTY_DEFAULT_GRAMMAR = "[useDefaultGrammar]";
  String MARKUP_PROPERTY_NO_ROOT_SET = "[noRootSet]";

  /**
   * The name of the content property. Extremely useful if you method name is longer than 18 characters.
   *
   * @return name of the content bean property
   */
  String propertyName() default PROPERTY_NAME_USE_METHOD_NAME;

  /**
   * The length of a string property.
   *
   * @return the length of the string property
   */
  int stringLength() default STRING_PROPERTY_DEFAULT_LENGTH;

  /**
   * For XML properties you can define a custom grammar
   *
   * @return the custom grammar for the Markup property
   */
  String propertyXmlGrammar() default MARKUP_PROPERTY_DEFAULT_GRAMMAR;

  /**
   * The root element for markup property.
   *
   * @return the markup root element.
   */
  String propertyXmlRoot() default MARKUP_PROPERTY_NO_ROOT_SET;
}
