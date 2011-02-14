package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.processors.ContentBeanAnalyzer;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshaller;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAppointment;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAttendee;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Telekom .COM Relaunch 2011
 * User: aratas
 * Date: 01.02.2011
 * Time: 12:17:49
 */
public class DocTypeMarshalerTest {

  private DocTypeMarshaller marshaller = null;

  @Before
  public void setup() {
    ContentBeanAnalyzer analyzator = new ContentBeanAnalyzator();
    analyzator.addContentBean(CBGContent.class);
    analyzator.addContentBean(CBGAttendee.class);
    analyzator.addContentBean(CBGAppointment.class);

    try {
      analyzator.analyzeContentBeanInformation();
      //not ok, but ok here to use null as output stream
      marshaller = new DocTypeMarshaller(analyzator.getContentBeanRoots(), null);
    }
    catch (Exception e) {
      fail();
    }

  }

  /**
   * Check if marshaller is successfully created and
   */
  @Test
  public void testMarshalerBasic() {
    OutputStream os = new ByteArrayOutputStream();
    assertNotNull(marshaller);
    marshaller.setOutputStream(os);
    try {
      marshaller.marshallDoctype();
    }
    catch (Exception e) {
      fail();
    }

    String expectedXML = "<DocumentTypeModel xmlns=\"http://www.coremedia.com/2009/documenttypes\" Title=\"telekom-document-type\">\n" +
        "    <ImportGrammar Name=\"coremedia-richtext-1.0\"/>\n"+
        "    <XmlGrammar Name=\"simple.xsd\"/>\n" +
        "    <DocType Name=\"CBGContent\">\n" +
        "        <StringProperty Length=\"20\" Name=\"Description\"/>\n" +
        "    </DocType>\n" +
        "    <DocType Name=\"CBGAppointment\" Parent=\"CBGContent\">\n" +
        "        <LinkListProperty LinkType=\"CBGAttendee\" Name=\"Attendees\" Max=\"" + Integer.MAX_VALUE + "\" Min=\"0\"/>\n" +
        "        <DateProperty Name=\"BeginDate\"/>\n" +
        "        <XmlProperty Grammar=\"simple.xsd\" Name=\"CustomXML\"/>\n" +
        "        <DateProperty Name=\"EndDate\"/>\n" +
        "        <IntProperty Name=\"NumberOfAttendees\"/>\n" +
        "        <LinkListProperty LinkType=\"CBGAttendee\" Name=\"Organizer\" Max=\"1\" Min=\"0\"/>\n" +
        "        <XmlProperty Grammar=\"coremedia-richtext-1.0\" Name=\"Text\"/>\n" +
        "    </DocType>\n" +
        "    <DocType Name=\"CBGAttendee\" Parent=\"CBGContent\"/>\n" +
        "</DocumentTypeModel>";

    try {
      DetailedDiff myDiff = new DetailedDiff(new Diff(expectedXML, os.toString()));
      assertTrue("XML similar " + myDiff.toString(),
          myDiff.similar());
      assertTrue("XML identical " + myDiff.toString(),
          myDiff.identical());
//      assertXMLEqual("comparing generated DocType XML to an expected XML", expectedXML, os.toString());
    }
    catch (Exception e) {
      fail();
    }

  }

}