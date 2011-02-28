package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzatorInternalException;
import com.coremedia.beanmodeller.processors.beaninformation.BlobPropertyInformation;
import com.coremedia.beanmodeller.processors.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.testcontentbeans.CBGRubbishBlobContent;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGImage;
import com.coremedia.beanmodeller.testutils.BeanModellerTestUtils;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertNotNull;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 15.02.11
 * Time: 17:31
 */
public class ContentBeanBlobPropertyTest {
  private ContentBeanAnalyzator contentBeanAnalyzator;
  private ContentBeanInformation cbgImageInformation;

  @Before
  public void setup() {
    contentBeanAnalyzator = new ContentBeanAnalyzator();
  }

  /**
   * Test should succeed, the return type defaults to AbstractContentBean.
   *
   * @throws NoSuchMethodException
   */
  @Test
  public void testNoExplicitReturnTypeParameter() {
    BlobPropertyInformation nonExplicitPropertyInformation = null;
    try {
      nonExplicitPropertyInformation = new BlobPropertyInformation(CBGImage.class.getMethod("getGenericBlob"));
    }
    catch (NoSuchMethodException e) {
      fail();
    }
    nonExplicitPropertyInformation.setAllowedMimeTypes("*/*");
    nonExplicitPropertyInformation.setDocumentTypePropertyName("genericBlob");

    contentBeanAnalyzator.addContentBean(CBGImage.class);
    try {
      contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      fail(e.getMessage());
    }

    ContentBeanInformation cbgImageInformation = null;
    try {
      cbgImageInformation = BeanModellerTestUtils.getContentBeans(contentBeanAnalyzator.getContentBeanRoots()).get("CBGImage");
    }
    catch (ContentBeanAnalyzatorInternalException e) {
      fail();
    }
    assertNotNull(cbgImageInformation);
    assertThat((Iterable<BlobPropertyInformation>) cbgImageInformation.getProperties(), hasItem(nonExplicitPropertyInformation));
  }

  /**
   * Test that the mime type specifications are correctly put in the property information.
   */
  @Test
  public void testExplicitPropertyInformation() {
    BlobPropertyInformation explicitPropertyInformation = null;
    try {
      explicitPropertyInformation = new BlobPropertyInformation(CBGImage.class.getMethod("getImage"));
    }
    catch (NoSuchMethodException e) {
      fail();
    }
    explicitPropertyInformation.setAllowedMimeTypes("image/*");
    explicitPropertyInformation.setDocumentTypePropertyName("image");

    contentBeanAnalyzator.addContentBean(CBGImage.class);
    try {
      contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      fail(e.getMessage());
    }

    ContentBeanInformation cbgImageInformation = null;
    try {
      cbgImageInformation = BeanModellerTestUtils.getContentBeans(contentBeanAnalyzator.getContentBeanRoots()).get("CBGImage");
    }
    catch (ContentBeanAnalyzatorInternalException e) {
      fail();
    }
    assertNotNull(cbgImageInformation);
    assertThat((Iterable<BlobPropertyInformation>) cbgImageInformation.getProperties(), hasItem(explicitPropertyInformation));
  }

  /**
   * Test if invalid mime type specifications are correctly handled.
   */
  @Test
  public void testRubbishContentBlob() {
    contentBeanAnalyzator.addContentBean(CBGRubbishBlobContent.class);
    try {
      contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      assertTrue(BeanModellerTestUtils.analyzationErrorContainsMessage(e, ContentBeanAnalyzationException.INVALID_MIME_TYPE_MESSAGE));
      return;
    }
    fail();
  }
}
