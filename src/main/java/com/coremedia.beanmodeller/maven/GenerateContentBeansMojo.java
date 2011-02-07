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

  /**
   * Where the generated content beans should be saved
   *
   * @parameter default-value="${project.build.directory}/generated-sources/beanmodeller"
   */
  private String targetPath;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    Set<ContentBeanInformation> roots = analyzeContentBeans();

    createContentBeanImplementations(roots);
  }

  private void createContentBeanImplementations(Set<ContentBeanInformation> roots) throws MojoFailureException {
    ContentBeanCodeGenerator generator = new ContentBeanCodeGenerator();
    generator.setPackageName(targetPackage);
    JCodeModel codeModel = generator.generateCode(roots);
    File targetDirectory = null;
    try {
      targetDirectory = getTargetDirectory();
    }
    catch (PluginException e) {
      throw new MojoFailureException("There was a problem with the target path", e);
    }
    try {
      CodeWriter output = new FileCodeWriter(targetDirectory);
      codeModel.build(output);
    }
    catch (IOException e) {
      throw new MojoFailureException("Unable to write content bean code", e);
    }
  }

  private Set<ContentBeanInformation> analyzeContentBeans() throws MojoFailureException, MojoExecutionException {
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
    return roots;
  }

  public File getTargetDirectory() throws PluginException {
    File result = new File(targetPath);
    if (!result.exists()) {
      throw new PluginException("The target path \'" + targetPath + "\' for the generated beans does not exist");
    }
    if (!result.isDirectory()) {
      throw new PluginException("The target path \'" + targetPath + "\' for the generated beans is no directory");
    }
    if (!result.canWrite()) {
      throw new PluginException("The target path \'" + targetPath + "\' for the generated beans is not writeable");
    }
    return result;
  }
}
