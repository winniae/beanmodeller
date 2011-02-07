package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.processors.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.codegenerator.ContentBeanCodeGenerator;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.FileCodeWriter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 07.02.11
 * Time: 11:23
 *
 * @goal generate-contentbeans
 */
public class GenerateContentBeansMojo extends AbstractBeanModellerMojo {

  /**
   * The target name of the generated content bean implementations.
   *
   * @parameter required = true, description = "Target directory for the generated code.")
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

    if (targetPackage == null) {
      throw new MojoFailureException("You must provide a package name for the content beans");
    }

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
    MavenProject project = getProject();
    //if we are running in a project
    if (project != null) {
      project.addCompileSourceRoot(targetPath);
    }
    else {
      getLog().warn("Not running in a maven project - source will most probably not be compiled!");
    }
  }

  public File getTargetDirectory() throws PluginException {
    File result = new File(targetPath);
    if (!result.exists()) {
      if (!result.mkdirs()) {
        throw new PluginException("The target path \'" + targetPath + "\' for the generated beans does not exist and cannot be generated");
      }
    }
    if (!result.isDirectory()) {
      throw new PluginException("The target path \'" + targetPath + "\' for the generated beans is no directory");
    }
    if (!result.canWrite()) {
      throw new PluginException("The target path \'" + targetPath + "\' for the generated beans is not writeable");
    }
    return result;
  }

  public String getTargetPackage() {
    return targetPackage;
  }

  public void setTargetPackage(String targetPackage) {
    this.targetPackage = targetPackage;
  }

  public String getTargetPath() {
    return targetPath;
  }

  public void setTargetPath(String targetPath) {
    this.targetPath = targetPath;
  }
}
