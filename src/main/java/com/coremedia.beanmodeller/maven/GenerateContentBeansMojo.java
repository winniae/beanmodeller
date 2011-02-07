package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.processors.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.ContentBeanAnalyzerException;
import com.coremedia.beanmodeller.processors.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.codegenerator.ContentBeanCodeGenerator;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.FileCodeWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 07.02.11
 * Time: 11:23
 *
 * @goal
 */
public class GenerateContentBeansMojo extends AbstractMojo {

  /**
   * Path for searching abstract content beans.
   *
   * @parameter
   */
  private String beanPackage;

  /**
   * The target name of the generated content bean implementations.
   *
   * @parameter
   */
  private String targetPackage;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    //create the analyzator
    ContentBeanAnalyzator analyzer = new ContentBeanAnalyzator();
    //set the logging
    analyzer.setLog(getLog());
    analyzer.findContentBeans(beanPackage);
    try {
      analyzer.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      throw new MojoFailureException("Unable to analyze the content beans", e);
    }
    Set<ContentBeanInformation> roots;
    try {
      roots = analyzer.getContentBeanRoots();
    }
    catch (ContentBeanAnalyzerException e) {
      throw new MojoExecutionException("This should have never happened", e);
    }
    ContentBeanCodeGenerator generator = new ContentBeanCodeGenerator();
    generator.setPackageName(targetPackage);
    JCodeModel codeModel = generator.generateCode(roots);
    File targetDirectory = getTargetDirectory();
    try {
      CodeWriter output = new FileCodeWriter(targetDirectory);
    }
    catch (IOException e) {
      throw new MojoFailureException("Unabe to write content bean code", e);
    }
  }

  public File getTargetDirectory() {
    return null;
  }
}
