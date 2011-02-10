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
public abstract class CBGStringPrpANeg extends AbstractContentBean {

  /**
   * Test negative stringLength definition.
   *
   * @return The description for a content.
   */
  @ContentProperty(stringLength = -23)
  protected abstract String getDescription();
}
