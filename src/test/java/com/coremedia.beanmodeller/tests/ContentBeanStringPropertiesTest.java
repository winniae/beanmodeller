package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.processors.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.ContentBeanAnalyzerException;
import com.coremedia.beanmodeller.processors.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.StringPropertyInformation;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.testcontentbeans.CBGStringPrpANeg;
import com.coremedia.beanmodeller.testcontentbeans.CBGStringPrpANoL;
import com.coremedia.beanmodeller.testcontentbeans.CBGStringPrpNoA;
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
public class ContentBeanStringPropertiesTest {

  ContentBeanAnalyzator contentBeanAnalyzator;
  StringPropertyInformation stringProperty;

  public static final int DEFAULT_STRING_PROPERTY_LENGTH = 34;
  private Class<CBGStringPrpNoA> stringPropertyBeanClass = CBGStringPrpNoA.class;
  private static final String METHOD_PREFIX = "get";

  @Before
  public void setup() throws NoSuchMethodException {

    contentBeanAnalyzator = new ContentBeanAnalyzator();

    contentBeanAnalyzator.setPropertyDefaultStringLength(DEFAULT_STRING_PROPERTY_LENGTH);

    String descriptionName = "Description";
    Method descriptionMethod = stringPropertyBeanClass.getDeclaredMethod(METHOD_PREFIX + descriptionName);

    stringProperty = new StringPropertyInformation(descriptionMethod);
    stringProperty.setDocumentTypePropertyName(descriptionName);
    stringProperty.setLength(20);
  }

  @Test
  /**
   * No annotation on method. This is valid. Check defined default!
   */
  public void testNoAnnotation() {

    contentBeanAnalyzator.addContentBean(stringPropertyBeanClass);

    try {
      contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      fail();
    }

    ContentBeanInformation cbgContent = null;
    try {
      cbgContent = BeanModellerTestUtils.getContentBeans(contentBeanAnalyzator.getContentBeanRoots()).get("CBGStringPrpNoA");
    }
    catch (ContentBeanAnalyzerException e) {
      fail();
    }

    stringProperty.setLength(DEFAULT_STRING_PROPERTY_LENGTH); // should be the default value definded in analyzer
    assertThat((Iterable<StringPropertyInformation>) cbgContent.getProperties(), hasItem(stringProperty));
  }

  @Test
  public void testAnnotationButNoLength() throws NoSuchMethodException {

    String descriptionName = "Description";
    Method descriptionMethod = CBGStringPrpANoL.class.getDeclaredMethod(METHOD_PREFIX + descriptionName);
    StringPropertyInformation myStringProperty = new StringPropertyInformation(descriptionMethod);
    myStringProperty.setDocumentTypePropertyName(descriptionName);
    myStringProperty.setLength(20);

    contentBeanAnalyzator.addContentBean(CBGStringPrpANoL.class);

    try {
      contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      fail();
    }

    ContentBeanInformation cbgContent = null;
    try {
      cbgContent = BeanModellerTestUtils.getContentBeans(contentBeanAnalyzator.getContentBeanRoots()).get("CBGStringPrpANoL");
    }
    catch (ContentBeanAnalyzerException e) {
      fail();
    }

    myStringProperty.setLength(DEFAULT_STRING_PROPERTY_LENGTH); // should be the default value definded in analyzer
    assertThat((Iterable<StringPropertyInformation>) cbgContent.getProperties(), hasItem(myStringProperty));
  }

  @Test
  public void testAnnotationButNegativeLength() {
    boolean exceptionWasThrown = false;

    contentBeanAnalyzator.addContentBean(CBGStringPrpANeg.class);

    try {
      contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionWasThrown = true;
    }

    assertTrue("Exception should have been thrown.", exceptionWasThrown);
  }
}
