package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;
import com.coremedia.cap.common.Blob;
import com.coremedia.objectserver.beans.AbstractContentBean;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 15.02.11
 * Time: 18:01
 */
@ContentBean(doctypeName = "rubbish")
public abstract class CBGRubbishBlobContent extends AbstractContentBean {

  @ContentProperty(propertyBlobMimeType = "complete_rubbish")
  public abstract Blob getRubbishBlob();
}
