package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentBean;

import java.util.Calendar;
import java.util.List;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * Date: Jan 27, 2011
 * Time: 9:46:59 AM
 */
@ContentBean
public abstract class CBGAppointment extends CBGContent {

  /**
   * Abstract Method. Will be implemented by Beangenerator.
   *
   * @return Number of Attendees from Content.
   */
  abstract Integer getNumberOfAttendees();

  /**
   * Get Begin and End Dates from Content.
   *
   * @return Date when the Appointment begins.
   */
  abstract Calendar getBeginDate();

  /**
   * Get Begin and End Dates from Content.
   *
   * @return Date when the Appointment ends..
   */
  abstract Calendar getEndDate();


  /**
   * @return Duration of Appointment in Milliseconds.
   */
  public Long getAppointmentDuration() {
    return getEndDate().getTimeInMillis() - getBeginDate().getTimeInMillis();
  }


  /**
   * get List of all Attendees from Content.
   * <p/>
   * No other restrictions.
   *
   * @return List of all Attendees.
   */
  abstract List<CBGAttendee> getAttendees();
}
