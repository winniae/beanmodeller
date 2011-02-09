package com.coremedia.beanmodeller.tests.maven;

import com.coremedia.beanmodeller.maven.GenerateContentBeansMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 07.02.11
 * Time: 15:22
 */
public class GenerateContentBeansMojoTest extends AbstractMojoTestCase {

  private static final String CONTENTBEANS_XML_PATH = "target/test-content-beans/spring/contentbeans.xml";
  private static final String CONTENTBEANS_XML_PATH_TESTREFERENCE = "src/test/resources/unit/test-project/contentbeans.xml";


  /**
   * {@inheritDoc}
   */
  protected void setUp() throws Exception {
    // required
    super.setUp();
    File pom = getTestFile("src/test/resources/unit/test-project/pom-generate-contentbeans.xml");
    assertNotNull(pom);
    assertTrue(pom.exists());

    GenerateContentBeansMojo generateDoctypesMojo = (GenerateContentBeansMojo) lookupMojo("generate-contentbeans", pom);
    assertNotNull(generateDoctypesMojo);
    generateDoctypesMojo.execute();

  }

  /**
   * {@inheritDoc}
   */
  protected void tearDown() throws Exception {
    // required
    super.tearDown();
  }

  /**
   * @throws Exception if any
   */
  public void testSomething() throws Exception {
    // setUp
  }

  @Test
  public void testContentBeanSpringConfig() {
    // ignore whitespaces
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setNormalizeWhitespace(true);

    try {
      final FileReader referenceXML = new FileReader(new File(CONTENTBEANS_XML_PATH_TESTREFERENCE));
      final FileReader testXML = new FileReader(new File(CONTENTBEANS_XML_PATH));
      DetailedDiff myDiff = new DetailedDiff(new Diff(
          referenceXML,
          testXML
      ));


      assertTrue("XML similar " + myDiff.toString(),
          myDiff.similar());
      assertTrue("XML identical " + myDiff.toString(),
          myDiff.identical());
    }
    catch (Exception e) {
      fail();
    }
  }
}
