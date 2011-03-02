package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.beaninformation.ContentBeanHierarchy;
import com.coremedia.beanmodeller.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.beaninformation.IntegerPropertyInformation;
import com.coremedia.beanmodeller.beaninformation.PropertyInformation;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzatorInternalException;
import com.coremedia.beanmodeller.testcontentbeans.CBGLongMthdAnno;
import com.coremedia.beanmodeller.testcontentbeans.CBGLongMthdFailA;
import com.coremedia.beanmodeller.testcontentbeans.CBGLongMthdFailD;
import com.coremedia.beanmodeller.testcontentbeans.CBGLongMthdFails;
import com.coremedia.beanmodeller.testutils.BeanModellerTestUtils;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static com.coremedia.beanmodeller.testutils.BeanModellerTestUtils.analyzationErrorContainsMessage;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * A test case to test if the names of objects and methods are correctly checked.
 */
public class ContentBeanPropertyNameLengthRestrictionTest {
  ContentBeanAnalyzator analyzator;
  private PropertyInformation integerProperty;
  private Class<CBGLongMthdFails> tooLongClass = CBGLongMthdFails.class;

  @Before
  public void setup() throws NoSuchMethodException {
    analyzator = new ContentBeanAnalyzator();

    String longJaneName = "MethodWithAnOverlyLongMethodNameOfFiftyCharacters";
    Method longJameMethod = tooLongClass.getDeclaredMethod("getMethodWithAnOverlyLongMethodNameOfFiftyCharacters");

    integerProperty = new IntegerPropertyInformation(longJameMethod);
    ((IntegerPropertyInformation) integerProperty).setDocumentTypePropertyName("longJane");
  }

  @Test
  public void testTooLongClassName() {
    analyzator.addContentBean(tooLongClass);
    boolean exceptionThrown = false;
    try {
      analyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionThrown = true;
      assertTrue(analyzationErrorContainsMessage(e, ContentBeanAnalyzationException.METHODNAME_TOO_LOGN_FOR_DOCTPYENAME_MESSAGE));
    }
    assertTrue("Exception wasn't thrown", exceptionThrown);
  }

  @Test
  public void testTooLongButCorrectlyAnnotatedClassName() throws NoSuchMethodException {
    String longJaneName = "MethodWithAnOverlyLongMethodNameOfFiftyCharactersButCorrectlyAnnotated";

    Method longJameMethod = CBGLongMthdAnno.class.getDeclaredMethod("getMethodWithAnOverlyLongMethodNameOfFiftyCharactersButCorrectlyAnnotated");

    IntegerPropertyInformation myIntegerProperty = new IntegerPropertyInformation(longJameMethod);
    ((IntegerPropertyInformation) myIntegerProperty).setDocumentTypePropertyName("LongJane");

    analyzator.addContentBean(CBGLongMthdAnno.class);
    ContentBeanHierarchy hierarchy = null;
    try {
      hierarchy = analyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      fail();
    }

    ContentBeanInformation information = null;
    try {
      information = BeanModellerTestUtils.getContentBeans(hierarchy.getRootBeanInformation()).get("CBGLongMthdAnno");
    }
    catch (ContentBeanAnalyzatorInternalException e) {
      fail();
    }
    assertThat((Iterable<IntegerPropertyInformation>) information.getProperties(), hasItem(myIntegerProperty));
    //if there was no exception everything was ok and the correct annotation was used
    //we did not want to test more
  }

  @Test
  public void testTooLongClassNameEvenWithAnnotation() {
    analyzator.addContentBean(CBGLongMthdFailA.class);
    boolean exceptionThrown = false;
    try {
      analyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionThrown = true;
      assertTrue(analyzationErrorContainsMessage(e, ContentBeanAnalyzationException.METHODNAME_TOO_LOGN_FOR_DOCTPYENAME_MESSAGE));
    }
    assertTrue("Exception wasn't thrown", exceptionThrown);
  }

  @Test
  public void testTooLongClassNameEvenWithAnnotationTwice() {
    analyzator.addContentBean(CBGLongMthdFailD.class);
    boolean exceptionThrown = false;
    try {
      analyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionThrown = true;
      assertTrue(analyzationErrorContainsMessage(e, ContentBeanAnalyzationException.DUPLICATE_PROPERTY_NAMES_MESSAGES));
    }
    assertTrue("Exception wasn't thrown", exceptionThrown);
  }
}
