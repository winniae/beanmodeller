package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.beaninformation.ContentBeanHierarchy;
import com.coremedia.beanmodeller.beaninformation.GrammarInformation;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshaller;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshallerException;
import com.coremedia.beanmodeller.processors.doctypegenerator.XSDCopyier;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAppointment;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGArticle;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAttendee;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGImage;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGSpecialArticle;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGSpecialAspectArticle;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;

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

  public static final String TARGET_XSD_TEST_PATH = "target/xsd-test";
  private DocTypeMarshaller marshaller = null;

  @Before
  public void setup() {
    ContentBeanAnalyzator analyzator = new ContentBeanAnalyzator();
    analyzator.addContentBean(CBGContent.class);
    analyzator.addContentBean(CBGAttendee.class);
    analyzator.addContentBean(CBGAppointment.class);
    analyzator.addContentBean(CBGImage.class);
    analyzator.addContentBean(CBGArticle.class);
    analyzator.addContentBean(CBGSpecialArticle.class);
    analyzator.addContentBean(CBGSpecialAspectArticle.class);

    try {
      ContentBeanHierarchy hierarchy = analyzator.analyzeContentBeanInformation();
      //not ok, but ok here to use null as output stream
      marshaller = new DocTypeMarshaller(hierarchy.getRootBeanInformation(), null);
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
    OutputStream os = marshalToOutputStream();

    String expectedXML = "<DocumentTypeModel xmlns=\"http://www.coremedia.com/2009/documenttypes\" Title=\"beanmodeller-document-type\">\n" +
        "    <ImportGrammar Name=\"coremedia-richtext-1.0\"/>\n" +
        "    <XmlGrammar Name=\"simple.dtd\" PublicId=\"-//simple//DTD//EN\" Root=\"simple\" SystemId=\"classpath:xml_schema_definitions/simple.dtd\"/>\n" +
        "    <XmlSchema Name=\"simple.xsd\" Language=\"http://www.w3.org/2001/XMLSchema\" SchemaLocation=\"classpath:xml_schema_definitions/simple.xsd\"/>\n" +
        "    <ImportGrammar Name=\"coremedia-struct-2008\"/>\n" +
        "    <ImportDocType Name=\"CMArticle\"/>\n" +
        "    <ImportDocType Name=\"CBGSpecialArt\"/>\n" +
        "    <DocType Name=\"CBGContent\" Abstract=\"true\">\n" +
        "        <StringProperty Length=\"20\" Name=\"description\"/>\n" +
        "    </DocType>\n" +
        "    <DocType Name=\"CBGAppointment\" Parent=\"CBGContent\">\n" +
        "        <LinkListProperty LinkType=\"CBGAttendee\" Name=\"attendees\" Max=\"" + Integer.MAX_VALUE + "\" Min=\"0\"/>\n" +
        "        <DateProperty Name=\"beginDate\"/>\n" +
        "        <XmlProperty Grammar=\"coremedia-richtext-1.0\" Name=\"customRichtext\"/>\n" +
        "        <XmlProperty Grammar=\"coremedia-struct-2008\" Name=\"customStruct\"/>\n" +
        "        <XmlProperty Grammar=\"simple.xsd\" Name=\"customXML\"/>\n" +
        "        <XmlProperty Grammar=\"simple.dtd\" Name=\"customXML2\"/>\n" +
        "        <DateProperty Name=\"endDate\"/>\n" +
        "        <IntProperty Name=\"numberOfAttendees\"/>\n" +
        "        <LinkListProperty LinkType=\"CBGAttendee\" Name=\"organizer\" Max=\"1\" Min=\"0\"/>\n" +
        "        <XmlProperty Grammar=\"coremedia-richtext-1.0\" Name=\"text\"/>\n" +
        "    </DocType>\n" +
        "    <DocType Name=\"CBGAttendee\" Parent=\"CBGContent\">\n" +
        "        <IntProperty Name=\"fun\"/>\n" +
        "    </DocType>\n" +
        "    <DocType Name=\"CBGImage\" Parent=\"CBGContent\">\n" +
        "        <BlobProperty MimeType=\"*/*\" Name=\"genericBlob\" />\n" +
        "        <BlobProperty MimeType=\"image/*\" Name=\"image\" />\n" +
        "    </DocType>\n" +
        "    <DocTypeAspect TargetType=\"CMArticle\">\n" +
        "        <BlobProperty MimeType=\"*/*\" Name=\"data\"/>\n" +
        "        <IntProperty Name=\"disabled\"/>\n" +
        "        <StringProperty Length=\"32\" Name=\"externalId\"/>\n" +
        "        <IntProperty Name=\"extIdInt\"/>\n" +
        "        <StringProperty Length=\"128\" Name=\"linkUrl\"/>\n" +
        "        <StringProperty Length=\"23\" Name=\"longer\"/>\n" +
        "        <LinkListProperty LinkType=\"CMArticle\" Name=\"master\" Max=\"1\" Min=\"0\"/>\n" +
        "        <IntProperty Name=\"nice\"/>\n" +
        "    </DocTypeAspect>\n" +
        "    <DocType Name=\"CBGSpecArticle\" Parent=\"CMArticle\">\n" +
        "        <IntProperty Name=\"extIdInt\" Override=\"true\"/>\n" +
        "        <LinkListProperty LinkType=\"CBGImage\" Name=\"images\" Max=\"2147483647\" Min=\"0\"/>\n" +
        "        <StringProperty Length=\"34\" Name=\"longer\" Override=\"true\"/>\n" +
        "        <StringProperty Length=\"32\" Name=\"specialSensation\"/>\n" +
        "    </DocType>\n" +
        "    <DocTypeAspect TargetType=\"CBGSpecialArt\"/>\n" +
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

  private OutputStream marshalToOutputStream() {
    OutputStream os = new ByteArrayOutputStream();
    assertNotNull(marshaller);
    marshaller.setOutputStream(os);
    try {
      marshaller.marshallDoctype();
    }
    catch (Exception e) {
      fail();
    }
    return os;
  }

  @Test
  public void testXSDCopy() {
    marshalToOutputStream();
    Map<String, GrammarInformation> foundMarkupSchemaDefinitions = marshaller.getFoundMarkupSchemaDefinitions();
    XSDCopyier xsdCopyier = new XSDCopyier(TARGET_XSD_TEST_PATH);
    try {
      xsdCopyier.copyXSD(foundMarkupSchemaDefinitions);
    }
    catch (DocTypeMarshallerException e) {
      fail();
    }
  }
}
