package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;
import com.coremedia.xml.Markup;

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
   * A simple XML property to ensure that a custom xsd is used.
   *
   * @return the custom XML
   */
  @ContentProperty(propertyXmlGrammar = "simple.xsd", propertyXmlRoot = "simple")
  abstract Markup getCustomXML();


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

  /**
   * get CoreMedia Markup from Content.
   *
   * @return always a Markup object containing some or no text.
   */
  abstract Markup getText();

  /**
   * The organizer organizes the appointment. He is still of type Attendee, though.
   * <p/>
   * Single LinkList item.
   *
   * @return One attendee object from Content
   */
  abstract CBGAttendee getOrganizer();
}