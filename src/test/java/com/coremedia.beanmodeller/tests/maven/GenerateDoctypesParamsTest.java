package com.coremedia.beanmodeller.tests.maven;

import com.coremedia.beanmodeller.maven.GenerateDoctypesMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * Telekom .COM Relaunch 2011
 * User: aratas
 * Date: 08.02.2011
 * Time: 10:17:07
 */
public class GenerateDoctypesParamsTest extends AbstractMojoTestCase {

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
  public void testInvalidDirectorySetting() throws Exception {
    File pom = getTestFile("src/test/resources/unit/test-project/pom-invalid-target-folder.xml");
    assertNotNull(pom);
    assertTrue(pom.exists());

    GenerateDoctypesMojo generateDoctypesMojo = (GenerateDoctypesMojo) lookupMojo("generate-doctypes", pom);
    assertNotNull(generateDoctypesMojo);

    try {
      generateDoctypesMojo.execute();
    }
    catch (MojoExecutionException e) {
      // should not come here
    }
    catch (MojoFailureException e) {
      assertEquals(GenerateDoctypesMojo.ERROR_CREATING_TARGET_DIRECTORY , e.getMessage());
      return;
    }
    fail("Didn't find the expected exception");
  }

  /**
   * @throws Exception if any
   */
  public void testInvalidFilename() throws Exception {
    
  }

}
