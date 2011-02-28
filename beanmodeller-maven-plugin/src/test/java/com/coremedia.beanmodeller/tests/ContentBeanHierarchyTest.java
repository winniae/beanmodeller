package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzatorInternalException;
import com.coremedia.beanmodeller.processors.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.testcontentbeans.CBGContentBeanInWreckedHierarchy;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAppointment;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAttendee;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;
import com.coremedia.beanmodeller.testutils.BeanModellerTestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This test the class hierarchy of the content beans, i.e., check parent child relationships of content beans
 */
public class ContentBeanHierarchyTest {

  ContentBeanAnalyzator analyzator = null;
  ContentBeanInformation rootBeanInformation = null;
  ContentBeanInformation childBeanInformation = null;
  Set<ContentBeanInformation> rootBeanInformationSet = null;

  /**
   * The test has exactly two content beans CBGContent is the root content beans,
   * and CBGAppointment extends it.
   */
  @Before
  public void setup() {
    analyzator = new ContentBeanAnalyzator();
    rootBeanInformation = null;
    childBeanInformation = null;
  }

  /**
   * Test relationships adding ROOT bean first
   */
  @Test
  public void testChildAndParentRootFirst() {
    // root content bean (parent)
    analyzator.addContentBean(CBGContent.class);
    // child content bean
    analyzator.addContentBean(CBGAppointment.class);
    analyzator.addContentBean(CBGAttendee.class);
    checkChildParentRelationship();
  }

  /**
   * Test relationships adding ROOT bean first
   */
  @Test
  public void testChildOnly() {
    // root content bean (parent)
    //not added this should be found by itself
    //analyzator.addContentBean(CBGContent.class);
    // child content bean
    analyzator.addContentBean(CBGAppointment.class);
    analyzator.addContentBean(CBGAttendee.class);
    checkChildParentRelationship();
  }

  @Test
  public void testWreckedHierarchy() {
    ContentBeanAnalyzator localAnalyzator = new ContentBeanAnalyzator();
    localAnalyzator.addContentBean(CBGContentBeanInWreckedHierarchy.class);
    boolean exceptionThrown = false;
    try {
      localAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionThrown = true;
      assertTrue(BeanModellerTestUtils.analyzationErrorContainsMessage(e, ContentBeanAnalyzationException.PROPERTY_NOT_IN_CB_MESSAGE));
    }
    assertTrue(exceptionThrown);
  }

  /**
   * Test relationships adding CHILD bean first
   */
  @Test
  public void testChildAndParentChildFirst() {
    // child content bean
    analyzator.addContentBean(CBGAppointment.class);
    // root content bean (parent)
    analyzator.addContentBean(CBGContent.class);
    analyzator.addContentBean(CBGAttendee.class);
    checkChildParentRelationship();
  }

  /**
   * test the relationship of parent-child relationship
   */
  private void checkChildParentRelationship() {
    try {
      analyzator.analyzeContentBeanInformation();
      rootBeanInformationSet = analyzator.getContentBeanRoots();
    }
    catch (ContentBeanAnalyzatorInternalException e) {
      // this should not run
      fail();
    }
    // get content bean information for the root bean, must return exactly one ContentBeanInformation
    rootBeanInformation = rootBeanInformationSet.iterator().next();
    // root bean must not have parent
    assertNull(rootBeanInformation.getParent());
    // make sure the root content bean is "CBGContent"
    assertEquals(rootBeanInformation.getDocumentName(), "CBGContent");
    // make sure "CBGContent" has exactly two childs
    assertEquals(2, rootBeanInformation.getChilds().size());

    //get child information
    for (ContentBeanInformation childBeanInformation : rootBeanInformation.getChilds()) {
      //make sure that our child bean is "CBGAppointment"
      assertThat(childBeanInformation.getDocumentName(), isOneOf("CBGAttendee", "CBGAppointment"));
      // child bean "CBGAppointment" must have parent
      assertNotNull(childBeanInformation.getParent());
      // child bean "CBGAppointment" must have no children
      assertEquals(0, childBeanInformation.getChilds().size());
    }
  }

}
