package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.beaninformation.ContentBeanHierarchy;
import com.coremedia.beanmodeller.beaninformation.ContentBeanInformation;
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
import static org.hamcrest.Matchers.hasItem;
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
  public static final String CHANGED_MARKUP_PROPERTY_GRAMMAR = "classpath:/xml_schema_definitions/simple.xsd";

  private Class<CBGMarkupAnno> markupPropertyBeanClass = CBGMarkupAnno.class;
  private MarkupPropertyInformation anotherTextProperty;
  private MarkupPropertyInformation otherGrammarProperty;

  @Before
  public void setup() throws NoSuchMethodException {

    contentBeanAnalyzator = new ContentBeanAnalyzator();

    contentBeanAnalyzator.setPropertyDefaultMarkupGrammar(DEFAULT_MARKUP_PROPERTY_GRAMMAR);


    String textName = "text";
    Method textMethod = markupPropertyBeanClass.getDeclaredMethod("getText");
    markupProperty = new MarkupPropertyInformation(textMethod);
    markupProperty.setDocumentTypePropertyName(textName);
    markupProperty.setGrammarName(DEFAULT_MARKUP_PROPERTY_GRAMMAR);

    String anotherTextName = "another";
    Method anotherTextMethod = markupPropertyBeanClass.getDeclaredMethod("getAnotherText");
    anotherTextProperty = new MarkupPropertyInformation(anotherTextMethod);
    anotherTextProperty.setDocumentTypePropertyName(anotherTextName);
    anotherTextProperty.setGrammarName(DEFAULT_MARKUP_PROPERTY_GRAMMAR);

    String otherGrammarName = "otherGrammar";
    Method otherGrammarMethod = markupPropertyBeanClass.getDeclaredMethod("getOtherGrammar");
    otherGrammarProperty = new MarkupPropertyInformation(otherGrammarMethod);
    otherGrammarProperty.setDocumentTypePropertyName(otherGrammarName);
    otherGrammarProperty.setGrammarName(CHANGED_MARKUP_PROPERTY_GRAMMAR);
  }

  @Test
  /**
   * No annotation on method. This is valid. Check defined default!
   */
  public void testNoAnnotation() {

    contentBeanAnalyzator.addContentBean(markupPropertyBeanClass);

    ContentBeanHierarchy hierarchy = null;
    try {
      hierarchy = contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      fail();
    }

    ContentBeanInformation cbgContent = null;
    try {
      cbgContent = BeanModellerTestUtils.getContentBeans(hierarchy.getRootBeanInformation()).get("CBGMarkupAnno");
    }
    catch (ContentBeanAnalyzatorInternalException e) {
      fail();
    }

    assertThat((Iterable<MarkupPropertyInformation>) cbgContent.getProperties(), hasItem(markupProperty));
  }

  @Test
  public void testAnnotationButNoGrammar() throws NoSuchMethodException {
    contentBeanAnalyzator.addContentBean(markupPropertyBeanClass);

    ContentBeanHierarchy hierarchy = null;
    try {
      hierarchy = contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      fail();
    }

    ContentBeanInformation cbgContent = null;
    try {
      cbgContent = BeanModellerTestUtils.getContentBeans(hierarchy.getRootBeanInformation()).get("CBGMarkupAnno");
    }
    catch (ContentBeanAnalyzatorInternalException e) {
      fail();
    }

    assertThat((Iterable<MarkupPropertyInformation>) cbgContent.getProperties(), hasItem(anotherTextProperty));
  }

  @Test
  public void testNonStandardGrammar() {
    contentBeanAnalyzator.addContentBean(markupPropertyBeanClass);

    ContentBeanHierarchy hierarchy = null;
    try {
      hierarchy = contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      fail();
    }

    ContentBeanInformation cbgContent = null;
    try {
      cbgContent = BeanModellerTestUtils.getContentBeans(hierarchy.getRootBeanInformation()).get("CBGMarkupAnno");
    }
    catch (ContentBeanAnalyzatorInternalException e) {
      fail();
    }

    assertThat((Iterable<MarkupPropertyInformation>) cbgContent.getProperties(), hasItem(otherGrammarProperty));
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
