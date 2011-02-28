package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.processors.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.ContentBeanAnalyzerException;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.beaninformation.LinkListPropertyInformation;
import com.coremedia.beanmodeller.testcontentbeans.CBGLinkListDflt;
import com.coremedia.beanmodeller.testcontentbeans.CBGLinkListFail;
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
public class ContentBeanLinkListPropertiesTest {

  ContentBeanAnalyzator contentBeanAnalyzator;
  LinkListPropertyInformation linkListPropertyInformation;

  private Class<CBGLinkListDflt> linkListPropertyBeanClass = CBGLinkListDflt.class;

  @Before
  public void setup() throws NoSuchMethodException {

    contentBeanAnalyzator = new ContentBeanAnalyzator();

    String attendeesName = "attendees";
    Method attendeesMethod = linkListPropertyBeanClass.getDeclaredMethod("getAttendees");

    linkListPropertyInformation = new LinkListPropertyInformation(attendeesMethod);
    linkListPropertyInformation.setDocumentTypePropertyName(attendeesName);
  }

  /**
   * Test should succeed, the return type defaults to AbstractContentBean.
   *
   * @throws NoSuchMethodException
   */
  @Test
  public void testNoExplicitReturnTypeParameter() {

    contentBeanAnalyzator.addContentBean(linkListPropertyBeanClass);

    try {
      contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      fail();
    }

    ContentBeanInformation cbgContent = null;
    try {
      cbgContent = BeanModellerTestUtils.getContentBeans(contentBeanAnalyzator.getContentBeanRoots()).get("CBGLinkListDflt");
    }
    catch (ContentBeanAnalyzerException e) {
      fail();
    }

    assertThat((Iterable<LinkListPropertyInformation>) cbgContent.getProperties(), hasItem(linkListPropertyInformation));
  }

  /**
   * //   * Should raise an error.
   */
  @Test
  public void testReturnTypeParameterIsNoContentBean() {
    boolean exceptionWasThrown = false;

    contentBeanAnalyzator.addContentBean(CBGLinkListFail.class);

    try {
      contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionWasThrown = true;
      assertTrue(BeanModellerTestUtils.analyzationErrorContainsMessage(e, ContentBeanAnalyzationException.LINKED_DOCTYPE_UNKNOWN));
    }

    assertTrue("Exception should have been thrown.", exceptionWasThrown);
  }
}
