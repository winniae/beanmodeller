package com.coremedia.beanmodeller.tests.maven;

import com.coremedia.beanmodeller.maven.GenerateAccessorizorBeansMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

    GenerateAccessorizorBeansMojo generateDoctypesMojo = (GenerateAccessorizorBeansMojo) lookupMojo("generate-contentbeans", pom);
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

  public void testCompileGeneratedCode() throws Exception {
    JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    StandardJavaFileManager sjfm = jc.getStandardFileManager(diagnostics, null, null);

    File javaDir = getTestFile("target/test-content-beans/com/coremedia/test/");
    assertThat(javaDir, is(notNullValue()));
    assertThat(javaDir.exists(), is(true));
    // getJavaFileObjects’ param is a vararg
    Iterable fileObjects = sjfm.getJavaFileObjects(javaDir.listFiles());

    // uhm, lets just compile into the same directory for now ..
    List<String> options = new LinkedList<String>();
    options.add("-d");
    options.add(getBasedir() + "/target/test-content-beans/");

    // add diagnostics to get error messages during compile


    final Boolean compilationResult = jc.getTask(null, sjfm, diagnostics, options, null, fileObjects).call();

    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
      System.out.format("Error on line %d in %s%n",
          diagnostic.getLineNumber(),
          diagnostic.getSource());
    }

    // Add more compilation tasks
    sjfm.close();

    assertThat(compilationResult, is(true));
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
