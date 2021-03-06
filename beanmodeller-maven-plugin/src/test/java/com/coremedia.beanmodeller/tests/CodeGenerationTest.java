package com.coremedia.beanmodeller.tests;

import com.coremedia.beanmodeller.beaninformation.ContentBeanHierarchy;
import com.coremedia.beanmodeller.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzatorInternalException;
import com.coremedia.beanmodeller.processors.codegenerator.ContentBeanCodeGenerator;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAppointment;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGArticle;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGAttendee;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGImage;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGSpecialArticle;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 02.02.11
 * Time: 11:25
 */
public class CodeGenerationTest {

  private static final String TEST_PACKAGE_NAME = "test.code";
  private ContentBeanAnalyzator analyzator;
  private ContentBeanCodeGenerator codegenerator;
  private Set<ContentBeanInformation> roots;
  private ContentBeanHierarchy hierarchy;

  @Before
  public void setup() throws ContentBeanAnalyzationException {
    analyzator = new ContentBeanAnalyzator();
    analyzator.addContentBean(CBGAppointment.class);
    analyzator.addContentBean(CBGAttendee.class);
    analyzator.addContentBean(CBGContent.class);
    analyzator.addContentBean(CBGImage.class);
    analyzator.addContentBean(CBGArticle.class);
    analyzator.addContentBean(CBGSpecialArticle.class);
    hierarchy = analyzator.analyzeContentBeanInformation();

    roots = hierarchy.getRootBeanInformation();

    codegenerator = new ContentBeanCodeGenerator();
    codegenerator.setPackageName(TEST_PACKAGE_NAME);
  }

  //doesn't succeed in Idea, but hangs in Status "terminated" and prevents further test execution.
  //@Test
  public void printGeneratedCode() throws ContentBeanAnalyzatorInternalException, IOException {
    assertNotNull(roots);
    assertFalse(roots.isEmpty());
    JCodeModel code = codegenerator.generateCode(roots);
    CodeWriter output = new SingleStreamCodeWriter(System.out);
    code.build(output);

    //this is a manual test only
    //if we would like to automate it we would have to check the source or compile and check the classes
    //too complicated for now
  }

  @Test
  public void testClassnameGeneration() throws ContentBeanAnalyzatorInternalException {
    assertNotNull(roots);
    assertFalse(roots.isEmpty());
    //root should have a element for CBGContent
    ContentBeanInformation cbgContentInformation = null;
    for (ContentBeanInformation beanInformation : roots) {
      if (CBGContent.class.equals(beanInformation.getContentBean())) {
        cbgContentInformation = beanInformation;
        //found it
        break;
      }
    }
    assertNotNull(cbgContentInformation);
    String targetname = TEST_PACKAGE_NAME + ".CBGContent" + codegenerator.IMPL_SUFFIX;
    String canonicalGeneratedClassName = codegenerator.getCanonicalGeneratedClassName(cbgContentInformation);
    assertTrue(targetname.equals(canonicalGeneratedClassName));
  }
}
