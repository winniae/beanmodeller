package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;
import com.coremedia.objectserver.beans.AbstractContentBean;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * Date: Jan 27, 2011
 * Time: 9:42:04 AM
 */
@ContentBean
public abstract class CBGStringPrpANoL extends AbstractContentBean {

  /**
   * Test missing stringLength definition but with existing Annotation. This should work and a reasonable default must be set.
   *
   * @return The description for a content.
   */
  @ContentProperty
  protected abstract String getDescription();
}
