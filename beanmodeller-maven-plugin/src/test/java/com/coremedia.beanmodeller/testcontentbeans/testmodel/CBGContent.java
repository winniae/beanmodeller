package com.coremedia.beanmodeller.testcontentbeans.testmodel;

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
public abstract class CBGContent extends AbstractContentBean {

  /**
   * Method is to be implemented by Beangenerator.
   * <p/>
   * The description for a content object is purely informative for an editor. It should not be used
   * on the delivery side.
   *
   * @return The description for a content.
   */
  @ContentProperty(stringLength = 20)
  protected abstract String getDescription();
}
