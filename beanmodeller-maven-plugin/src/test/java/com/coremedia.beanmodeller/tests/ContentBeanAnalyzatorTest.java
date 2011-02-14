package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.processors.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.ContentBeanAnalyzerException;
import com.coremedia.beanmodeller.processors.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.testcontentbeans.CBGBeanPretendingBeeingCBGContent;
import com.coremedia.beanmodeller.testcontentbeans.CBGContentNotExtendingAbstractContentBean;
import com.coremedia.beanmodeller.testcontentbeans.NotCBGContentBean;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAppointment;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAttendee;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;
import com.coremedia.beanmodeller.testutils.BeanModellerTestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static com.coremedia.beanmodeller.testutils.BeanModellerTestUtils.analyzationErrorContainsMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ContentBeanAnalyzatorTest {
  ContentBeanAnalyzator analyzator;


  @Before
  public void setup() {
    analyzator = new ContentBeanAnalyzator();
  }

  @Test
  public void testLifecycle() {
    boolean exceptionThrown = false;
    try {
      analyzator.getContentBeanRoots();
    }
    catch (ContentBeanAnalyzerException e) {
      exceptionThrown = true;
      assertThat(e.getMessage(), containsString(ContentBeanAnalyzerException.LIFECYCLE_VIOLATION));
    }

    assertThat(exceptionThrown, is(true));
  }

  @Test
  public void testLifecycle2() {
    boolean exceptionThrown = false;
    try {
      BeanModellerTestUtils.getContentBeans(analyzator.getContentBeanRoots());
    }
    catch (ContentBeanAnalyzerException e) {
      exceptionThrown = true;
      assertThat(e.getMessage(), containsString(ContentBeanAnalyzerException.LIFECYCLE_VIOLATION));
    }

    assertThat(exceptionThrown, is(true));
  }

  @Test
  public void testInheritedBeans() {
    assertNotNull(analyzator);
    //we do not inject this - it should be found automatically
    //analyzator.addContentBean(CBGContent.class);
    analyzator.addContentBean(CBGAppointment.class);
    analyzator.addContentBean(CBGAttendee.class);
    analyzator.addContentBean(NotCBGContentBean.class);

    try {
      analyzator.analyzeContentBeanInformation();
      Set<ContentBeanInformation> contentBeanRoots = analyzator.getContentBeanRoots();
      assertNotNull(contentBeanRoots);
      assertEquals(2, contentBeanRoots.size());
    }
    catch (ContentBeanAnalyzerException e) {
      //no exception should be thrown
      fail();
    }
  }

  @Test
  public void testNameDuplication() {
    analyzator.addContentBean(CBGContent.class);
    analyzator.addContentBean(CBGBeanPretendingBeeingCBGContent.class);

    boolean exceptionThrown = false;
    try {
      analyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionThrown = true;
      assertTrue(BeanModellerTestUtils.analyzationErrorContainsMessage(e, ContentBeanAnalyzationException.DUPLICATE_DOCTYPE_NAMES_MESSAGE));
    }
    assertTrue(exceptionThrown);
  }

  //TODO: test the inheritance

  @Test
  public void testInheritance() {
    analyzator.addContentBean(CBGContentNotExtendingAbstractContentBean.class);
    boolean exceptionThrown = false;
    try {
      analyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionThrown = true;
      assertTrue(analyzationErrorContainsMessage(e, ContentBeanAnalyzationException.NOT_INHERITING_ABSTRACT_CONTENT_BEAN_MESSAGE));
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testFlattenedBeans() {
    assertNotNull(analyzator);
    analyzator.addContentBean(CBGAppointment.class);
    analyzator.addContentBean(CBGAttendee.class);

    try {
      analyzator.analyzeContentBeanInformation();
      Map<String, ContentBeanInformation> contentBeans = BeanModellerTestUtils.getContentBeans(analyzator.getContentBeanRoots());
      assertNotNull(contentBeans);
      assertThat(contentBeans.size(), is(3));
      assertThat(contentBeans.keySet(), hasItem("CBGAppointment"));
      assertThat(contentBeans.keySet(), hasItem("CBGContent"));
    }
    catch (ContentBeanAnalyzerException e) {
      //no exception should be thrown
      fail();
    }
  }
}
