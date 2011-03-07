package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;
import com.coremedia.xml.Markup;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 */
@ContentBean
public abstract class CBGMarkupAnno extends CBGContent {

  public abstract Markup getText();

  @ContentProperty(propertyName = "another")
  public abstract Markup getAnotherText();

  /**
   * Markup with Annotation for custom-grammar.
   *
   * @return .
   */
  @ContentProperty(propertyXmlGrammar = "classpath:/xml_schema_definitions/simple.xsd")
  public abstract Markup getOtherGrammar();
}
