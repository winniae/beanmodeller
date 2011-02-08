package com.coremedia.beanmodeller.tests.maven;

import com.coremedia.beanmodeller.maven.GenerateDoctypesMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: aratas
 * Date: 26.01.2011
 * Time: 11:35:41
 * To change this template use File | Settings | File Templates.
 */
public class GenerateDoctypesMojoTest extends AbstractMojoTestCase {
  /**
   * {@inheritDoc}
   */
  protected void setUp() throws Exception {
    // required
    super.setUp();
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
    File pom = getTestFile("src/test/resources/unit/test-project/pom.xml");
    assertNotNull(pom);
    assertTrue(pom.exists());

    GenerateDoctypesMojo generateDoctypesMojo = (GenerateDoctypesMojo) lookupMojo("generate-doctypes", pom);
    assertNotNull(generateDoctypesMojo);
    generateDoctypesMojo.setAbstractBeanPath("com.coremedia.beanmodeller.testcontentbeans.testmodel");
    generateDoctypesMojo.setDocTypeTargetFileName("doctypes.xml");
    generateDoctypesMojo.setDocTypeTargetPath("target/test/");
    generateDoctypesMojo.execute();
  }

}
