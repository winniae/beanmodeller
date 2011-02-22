package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;

import java.util.List;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 */
@ContentBean
public abstract class CBGLinkListDflt extends CBGContent {

  /**
   * No explicit return type given. This is allowed behavior.
   *
   * @return List of all Attendees.
   */
  public abstract List getAttendees();
}
