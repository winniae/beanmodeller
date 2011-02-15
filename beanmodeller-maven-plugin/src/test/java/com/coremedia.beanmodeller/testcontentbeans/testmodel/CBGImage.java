package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;
import com.coremedia.cap.common.Blob;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 14.02.11
 * Time: 19:11
 */
@ContentBean
public abstract class CBGImage extends CBGContent {

  /**
   * An arbitrary Image
   * @return an arbitrary image
   */
  @ContentProperty (propertyBlobMimeType = "image/*")
  public abstract Blob getImage();
}
