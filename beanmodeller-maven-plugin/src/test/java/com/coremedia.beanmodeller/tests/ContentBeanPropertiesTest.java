package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.processors.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.ContentBeanAnalyzerException;
import com.coremedia.beanmodeller.processors.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.DatePropertyInformation;
import com.coremedia.beanmodeller.processors.IntegerPropertyInformation;
import com.coremedia.beanmodeller.processors.LinkListPropertyInformation;
import com.coremedia.beanmodeller.processors.MarkupPropertyInformation;
import com.coremedia.beanmodeller.processors.StringPropertyInformation;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.testcontentbeans.CBGContentBeanInWreckedHierarchy;
import com.coremedia.beanmodeller.testcontentbeans.CBGUsesPrimitives;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAppointment;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAttendee;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;
import com.coremedia.beanmodeller.testutils.BeanModellerTestUtils;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * <p/>
 * Positive tests for all properties. Works on the "testmodel" classes.
 */
public class ContentBeanPropertiesTest {

  private static final String METHOD_PREFIX = "get";
  ContentBeanAnalyzator contentBeanAnalyzator;
  IntegerPropertyInformation integerProperty;
  StringPropertyInformation stringProperty;
  DatePropertyInformation datePropertyInformation_begin;
  DatePropertyInformation datePropertyInformation_end;
  LinkListPropertyInformation linkListPropertyInformation;
  MarkupPropertyInformation markupPropertyInformation;
  ContentBeanInformation cbgAppointment;
  ContentBeanInformation cbgContent;
  ContentBeanInformation cbgAttendee;

  @Before
  public void setup() throws NoSuchMethodException {

    contentBeanAnalyzator = new ContentBeanAnalyzator();
    cbgAppointment = null;

    Class<CBGAppointment> cbgAppointmentClass = CBGAppointment.class;

    String numberOfAttendeesName = "NumberOfAttendees";
    String beginDateName = "BeginDate";
    String endDateName = "EndDate";
    String descriptionName = "Description";
    String attendeesName = "Attendees";
    String textName = "Text";

    Method numberOfAttendeesMethod = cbgAppointmentClass.getDeclaredMethod(METHOD_PREFIX + numberOfAttendeesName);
    Method beginDateMethod = cbgAppointmentClass.getDeclaredMethod(METHOD_PREFIX + beginDateName);
    Method endDateMethod = cbgAppointmentClass.getDeclaredMethod(METHOD_PREFIX + endDateName);
    Method descriptionMethod = CBGContent.class.getDeclaredMethod(METHOD_PREFIX + descriptionName);
    Method attendeesMethod = cbgAppointmentClass.getDeclaredMethod(METHOD_PREFIX + attendeesName);
    Method textMethod = cbgAppointmentClass.getDeclaredMethod(METHOD_PREFIX + textName);

    integerProperty = new IntegerPropertyInformation(numberOfAttendeesMethod);
    integerProperty.setDocumentTypePropertyName(numberOfAttendeesName);

    datePropertyInformation_begin = new DatePropertyInformation(beginDateMethod);
    datePropertyInformation_begin.setDocumentTypePropertyName(beginDateName);

    datePropertyInformation_end = new DatePropertyInformation(endDateMethod);
    datePropertyInformation_end.setDocumentTypePropertyName(endDateName);

    stringProperty = new StringPropertyInformation(descriptionMethod);
    stringProperty.setDocumentTypePropertyName(descriptionName);
    stringProperty.setLength(20);

    markupPropertyInformation = new MarkupPropertyInformation(textMethod);
    markupPropertyInformation.setDocumentTypePropertyName(textName);

    contentBeanAnalyzator.addContentBean(cbgAppointmentClass);
    contentBeanAnalyzator.addContentBean(CBGAttendee.class);

    try {
      contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      fail();
    }

    try {
      cbgContent = BeanModellerTestUtils.getContentBeans(contentBeanAnalyzator.getContentBeanRoots()).get("CBGContent");
      assertTrue(cbgContent.getContentBean().equals(CBGContent.class));

      cbgAppointment = BeanModellerTestUtils.getContentBeans(contentBeanAnalyzator.getContentBeanRoots()).get("CBGAppointment");
      assertTrue(cbgAppointment.getContentBean().equals(cbgAppointmentClass));

      cbgAttendee = BeanModellerTestUtils.getContentBeans(contentBeanAnalyzator.getContentBeanRoots()).get("CBGAttendee");
      assertTrue(cbgAttendee.getContentBean().equals(CBGAttendee.class));
    }
    catch (ContentBeanAnalyzerException e) {
      fail();
    }

    linkListPropertyInformation = new LinkListPropertyInformation(attendeesMethod);
    linkListPropertyInformation.setDocumentTypePropertyName(attendeesName);
    linkListPropertyInformation.setLinkType(cbgAttendee);
    linkListPropertyInformation.setMin(0);
    linkListPropertyInformation.setMax(Integer.MAX_VALUE);
  }

  @Test
  public void testNumberProperty() {
    // information should contain both properties for date and integer
    assertThat((Iterable<IntegerPropertyInformation>) cbgAppointment.getProperties(), hasItem(integerProperty));
  }

  @Test
  public void testDateProperty() {
    // information should contain both properties for date and integer
    assertThat((Iterable<DatePropertyInformation>) cbgAppointment.getProperties(), hasItem(datePropertyInformation_begin));
    assertThat((Iterable<DatePropertyInformation>) cbgAppointment.getProperties(), hasItem(datePropertyInformation_end));

  }

  @Test
  public void testNonAbstractProperty() {
    contentBeanAnalyzator.addContentBean(CBGContentBeanInWreckedHierarchy.class);
    boolean exceptionThrown = false;
    try {
      contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionThrown = true;
      assertTrue(BeanModellerTestUtils.analyzationErrorContainsMessage(e, ContentBeanAnalyzationException.INVALID_PROPERTY_MESSAGE));
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testStringProperty() {
    // compares stringLength as well, as it is part of the equals method
    assertThat((Iterable<StringPropertyInformation>) cbgContent.getProperties(), hasItem(stringProperty));
  }

  @Test
  public void testLinkListProperty() {
    assertThat((Iterable<LinkListPropertyInformation>) cbgAppointment.getProperties(), hasItem(linkListPropertyInformation));
  }

  @Test
  public void testPrimitives() {
    contentBeanAnalyzator.addContentBean(CBGUsesPrimitives.class);
    boolean exceptionThrown = false;
    try {
      contentBeanAnalyzator.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      exceptionThrown = true;
      assertTrue(BeanModellerTestUtils.analyzationErrorContainsMessage(e, ContentBeanAnalyzationException.INVALID_RETURN_TYPES_MESSAGE));
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testMarkupProperty() {
    assertThat((Iterable<MarkupPropertyInformation>) cbgAppointment.getProperties(), hasItem(markupPropertyInformation));
  }
}