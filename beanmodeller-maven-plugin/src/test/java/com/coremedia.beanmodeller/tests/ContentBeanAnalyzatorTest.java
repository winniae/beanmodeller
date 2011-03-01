package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzatorInternalException;
import com.coremedia.beanmodeller.processors.beaninformation.ContentBeanHierarchy;
import com.coremedia.beanmodeller.processors.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.testcontentbeans.CBGBeanPretendingBeeingCBGContent;
import com.coremedia.beanmodeller.testcontentbeans.CBGContentNotExtendingAbstractContentBean;
import com.coremedia.beanmodeller.testcontentbeans.NotCBGContentBean;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAppointment;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAttendee;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGImage;
import com.coremedia.beanmodeller.testutils.BeanModellerTestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static com.coremedia.beanmodeller.testutils.BeanModellerTestUtils.analyzationErrorContainsMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
  public void testAbstractBeans() {
    analyzator.addContentBean(CBGContent.class);
    analyzator.addContentBean(CBGImage.class);
    ContentBeanHierarchy hierarchy = null;
    try {
      hierarchy = analyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      e.printStackTrace();
      fail();
    }
    Set<ContentBeanInformation> roots = hierarchy.getRootBeanInformation();
    assertTrue(roots.size() == 1);
    ContentBeanInformation content = roots.iterator().next();
    assertTrue(content.isAbstract());
    assertNotNull(content.getChilds());
    assertTrue(content.getChilds().size() == 1);
    ContentBeanInformation image = content.getChilds().iterator().next();
    assertNotNull(image);
    assertFalse(image.isAbstract());
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
      ContentBeanHierarchy hierarchy = analyzator.analyzeContentBeanInformation();
      Set<ContentBeanInformation> contentBeanRoots = hierarchy.getRootBeanInformation();
      assertNotNull(contentBeanRoots);
      assertEquals(2, contentBeanRoots.size());
    }
    catch (ContentBeanAnalyzatorInternalException e) {
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
      ContentBeanHierarchy hierarchy = analyzator.analyzeContentBeanInformation();
      Map<String, ContentBeanInformation> contentBeans = BeanModellerTestUtils.getContentBeans(hierarchy.getRootBeanInformation());
      assertNotNull(contentBeans);
      assertThat(contentBeans.size(), is(3));
      assertThat(contentBeans.keySet(), hasItem("CBGAppointment"));
      assertThat(contentBeans.keySet(), hasItem("CBGContent"));
    }
    catch (ContentBeanAnalyzatorInternalException e) {
      //no exception should be thrown
      fail();
    }
  }
}

