package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.processors.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.ContentBeanAnalyzerException;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.testcontentbeans.CBGContentClassWithAnOverlyLongClassNameOfFiftyCharacters;
import com.coremedia.beanmodeller.testcontentbeans.CBGContentClassWithAnOverlyLongClassNameOfMoreThanFiftyCharactersAndWrongAnnotated;
import com.coremedia.beanmodeller.testcontentbeans.CBGContentClassWithAnOverlyLongClassNameOfMoreThanFiftyCharactersButCorrectlyAnnotated;
import com.coremedia.beanmodeller.testutils.BeanModellerTestUtils;
import org.junit.Before;
import org.junit.Test;

import static com.coremedia.beanmodeller.testutils.BeanModellerTestUtils.analyzationErrorContainsMessage;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * A test case to test if the names of objects and methods are correctly checked.
 */
public class ContentBeanNameLengthRestrictionTest {
  ContentBeanAnalyzator analyzator;

  @Before
  public void setup() {
    analyzator = new ContentBeanAnalyzator();
  }

  @Test
  public void testTooLongClassName() {
    analyzator.addContentBean(CBGContentClassWithAnOverlyLongClassNameOfFiftyCharacters.class);
    boolean exceptionThrown = false;
    try {
      analyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionThrown = true;
      assertTrue(analyzationErrorContainsMessage(e, ContentBeanAnalyzationException.CLASSNAME_TOO_LOGN_FOR_DOCTPYENAME_MESSAGE));
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testTooLongButCorrectlyAnnotatedClassName() {
    analyzator.addContentBean(CBGContentClassWithAnOverlyLongClassNameOfMoreThanFiftyCharactersButCorrectlyAnnotated.class);
    try {
      analyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      fail();
    }

    ContentBeanInformation information = null;
    try {
      // CBGContentClassWithAnOverlyLongClassNameOfMoreThanFiftyCharactersButCorrectlyAnnotated
      // is named "LongJohn"
      information = BeanModellerTestUtils.getContentBeans(analyzator.getContentBeanRoots()).get("LongJohn");
    }
    catch (ContentBeanAnalyzerException e) {
      fail();
    }
    assertThat(information.getDocumentName(), is("LongJohn"));
    //if there was no exception everything was ok and the correct annotation was used
    //we did not want to test more
  }

  @Test
  public void testTooLongClassNameEvenWithAnnotation() {
    analyzator.addContentBean(CBGContentClassWithAnOverlyLongClassNameOfMoreThanFiftyCharactersAndWrongAnnotated.class);
    boolean exceptionThrown = false;
    try {
      analyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionThrown = true;
      assertTrue(analyzationErrorContainsMessage(e, ContentBeanAnalyzationException.CLASSNAME_TOO_LOGN_FOR_DOCTPYENAME_MESSAGE));
    }
    assertTrue(exceptionThrown);
  }
}
