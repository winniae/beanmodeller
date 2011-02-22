package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;

import java.util.List;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 */
@ContentBean
public abstract class CBGLinkListFail extends CBGContent {

  /**
   * Return Type is not of type ContentBean. Should raise an error.
   *
   * @return List of all Attendees.
   */
  public abstract List<NotCBGContentBean> getAttendees();
}
