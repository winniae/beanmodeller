package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.objectserver.beans.AbstractContentBean;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * Date: Jan 27, 2011
 * Time: 9:42:04 AM
 */
@ContentBean
public abstract class CBGStringPrpNoA extends AbstractContentBean {

  /**
   * Test string property definition without Annotation. This is valid and a reasonable default must be set.
   *
   * @return The description for a content.
   */
  protected abstract String getDescription();
}
