package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.beaninformation.ContentBeanHierarchy;
import com.coremedia.beanmodeller.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.beaninformation.GrammarInformation;
import com.coremedia.beanmodeller.beaninformation.MarkupPropertyInformation;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzatorInternalException;
import com.coremedia.beanmodeller.testcontentbeans.CBGMarkupAnno;
import com.coremedia.beanmodeller.testutils.BeanModellerTestUtils;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 */
public class ContentBeanMarkupPropertiesTest {

  ContentBeanAnalyzator contentBeanAnalyzator;
  MarkupPropertyInformation markupProperty;

  public static final String DEFAULT_MARKUP_PROPERTY_GRAMMAR = "coremedia-richtext-1.0";
  public static final String CHANGED_MARKUP_PROPERTY_GRAMMAR_LOCATION = "classpath:xml_schema_definitions/simple.xsd";
  public static final String CHANGED_MARKUP_PROPERTY_GRAMMAR_LOCATION2 = "classpath:xml_schema_definitions/simple2.xsd";
  public static final String CHANGED_MARKUP_PROPERTY_GRAMMAR_NAME = "simple.xsd";
  public static final String CHANGED_MARKUP_PROPERTY_GRAMMAR_NAME2 = "simple2.xsd";

  private Class<CBGMarkupAnno> markupPropertyBeanClass = CBGMarkupAnno.class;
  private MarkupPropertyInformation anotherTextProperty;
  private MarkupPropertyInformation otherGrammarProperty;
  private MarkupPropertyInformation multipleGrammarProperty;

  // Test value
  ContentBeanInformation cbgContent = null;

  @Before
  public void setup() throws NoSuchMethodException {

    contentBeanAnalyzator = new ContentBeanAnalyzator();

    contentBeanAnalyzator.setPropertyDefaultMarkupGrammar(DEFAULT_MARKUP_PROPERTY_GRAMMAR);


    String textName = "text";
    Method textMethod = markupPropertyBeanClass.getDeclaredMethod("getText");
    markupProperty = new MarkupPropertyInformation(textMethod);
    markupProperty.setDocumentTypePropertyName(textName);

    String anotherTextName = "another";
    Method anotherTextMethod = markupPropertyBeanClass.getDeclaredMethod("getAnotherText");
    anotherTextProperty = new MarkupPropertyInformation(anotherTextMethod);
    anotherTextProperty.setDocumentTypePropertyName(anotherTextName);

    String otherGrammarName = "otherGrammar";
    Method otherGrammarMethod = markupPropertyBeanClass.getDeclaredMethod("getOtherGrammar");
    otherGrammarProperty = new MarkupPropertyInformation(otherGrammarMethod);
    otherGrammarProperty.setDocumentTypePropertyName(otherGrammarName);
    GrammarInformation otherGrammarInfo = new GrammarInformation();
    otherGrammarInfo.setGrammarName(CHANGED_MARKUP_PROPERTY_GRAMMAR_NAME);
    otherGrammarInfo.addGrammarLocation(CHANGED_MARKUP_PROPERTY_GRAMMAR_LOCATION);
    otherGrammarProperty.setGrammarInformation(otherGrammarInfo);

    String multipleGrammarName = "multipleGrammar";
    Method multipleGrammarMethod = markupPropertyBeanClass.getDeclaredMethod("getMultipleGrammar");
    multipleGrammarProperty = new MarkupPropertyInformation(multipleGrammarMethod);
    multipleGrammarProperty.setDocumentTypePropertyName(multipleGrammarName);
    GrammarInformation multipleGrammarInfo = new GrammarInformation();
    multipleGrammarInfo.setGrammarName(CHANGED_MARKUP_PROPERTY_GRAMMAR_NAME2);
    multipleGrammarInfo.addGrammarLocation(CHANGED_MARKUP_PROPERTY_GRAMMAR_LOCATION);
    multipleGrammarInfo.addGrammarLocation(CHANGED_MARKUP_PROPERTY_GRAMMAR_LOCATION2);
    // add both grammar informations
    multipleGrammarProperty.setGrammarInformation(multipleGrammarInfo);


    // Analyzator Setup
    contentBeanAnalyzator.addContentBean(markupPropertyBeanClass);

    ContentBeanHierarchy hierarchy = null;
    try {
      hierarchy = contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      fail();
    }


    try {
      cbgContent = BeanModellerTestUtils.getContentBeans(hierarchy.getRootBeanInformation()).get("CBGMarkupAnno");
    }
    catch (ContentBeanAnalyzatorInternalException e) {
      fail();
    }
  }

  @Test
  /**
   * No annotation on method. This is valid. Check defined default!
   */
  public void testNoAnnotation() {
    assertThat((Iterable<MarkupPropertyInformation>) cbgContent.getProperties(), hasItem(markupProperty));
  }

  @Test
  public void testAnnotationButNoGrammar() throws NoSuchMethodException {
    assertThat((Iterable<MarkupPropertyInformation>) cbgContent.getProperties(), hasItem(anotherTextProperty));
  }

  @Test
  public void testNonStandardGrammar() {
    assertThat((Iterable<MarkupPropertyInformation>) cbgContent.getProperties(), hasItem(otherGrammarProperty));
  }

  @Test
  public void testMultipleNonStandardGrammar() {
    assertThat((Iterable<MarkupPropertyInformation>) cbgContent.getProperties(), hasItem(multipleGrammarProperty));
  }

//  @Test don't know how to test yet..

  public void testAnnotationButInvalidGrammar() {
    boolean exceptionWasThrown = false;

    contentBeanAnalyzator.addContentBean(markupPropertyBeanClass);

    try {
      contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionWasThrown = true;
    }

    assertTrue("Exception should have been thrown.", exceptionWasThrown);
  }
}
