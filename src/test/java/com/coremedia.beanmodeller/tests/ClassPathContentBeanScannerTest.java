package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.maven.MavenProcessor;
import com.coremedia.beanmodeller.processors.analyzator.ClassPathContentBeanScanner;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Telekom .COM Relaunch 2011
 * User: aratas
 * Date: 02.02.2011
 * Time: 11:09:06
 */
public class ClassPathContentBeanScannerTest extends MavenProcessor {

  ClassPathContentBeanScanner scanner = null;

  @Before
  public void setup() {
    scanner = new ClassPathContentBeanScanner();
  }

  @Test
  public void testPackageSearch() {
  Set<Class> candidateCBs = scanner.findCandidateContentBeanClasses("com.coremedia.beanmodeller.testcontentbeans.testmodel", getLog());
  // make sure it finds all three annotated beans in folder
  assertEquals(3, candidateCBs.size());
  }

  /**
   * Test the following:
   * - if scanner is able to find beans in subpackage
   * - if it ignores non-annotated classes
   */
  public void nonExistantComponentSearch () {
    Set<Class> candidateCBs = scanner.findCandidateContentBeanClasses("com.coremedia.beanmodeller.testcontentbeans", getLog());
    // make sure it finds all three annotated beans in folder and in subfolder
    assertEquals(17, candidateCBs.size());
  }
}
