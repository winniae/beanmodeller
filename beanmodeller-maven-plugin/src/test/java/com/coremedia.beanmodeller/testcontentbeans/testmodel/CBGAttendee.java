package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 */
@ContentBean
public abstract class CBGAttendee extends CBGContent {


  /**
   * Denote whether an attendee of this meeting would like to join proceed
   * with private meetings after a couple of drinks.
   *
   * @return Boolean or null if value is not set !
   */
  @ContentProperty
  public abstract Boolean isFun();

}
