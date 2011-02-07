package com.coremedia.beanmodeller.tests.maven;

import com.coremedia.beanmodeller.maven.GenerateContentBeansMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 07.02.11
 * Time: 15:22
 */
public class GenerateContentBeansMojoTest extends AbstractMojoTestCase {
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

    GenerateContentBeansMojo generateDoctypesMojo = (GenerateContentBeansMojo) lookupMojo("generate-contentbeans", pom);
    generateDoctypesMojo.setBeanPackage("com.coremedia.beanmodeller.testcontentbeans.testmodel");
    generateDoctypesMojo.setTargetPackage("com.coremedia.test");
    generateDoctypesMojo.setTargetPath("target/test");
    assertNotNull(generateDoctypesMojo);
    generateDoctypesMojo.execute();
  }
}
