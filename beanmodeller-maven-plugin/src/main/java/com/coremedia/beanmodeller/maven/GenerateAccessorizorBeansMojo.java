package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.codegenerator.ContentBeanCodeGenerator;
import com.coremedia.beanmodeller.processors.configgenerator.ContentBeansSpringXmlFreemarkerGenerator;
import com.coremedia.beanmodeller.processors.configgenerator.ContentBeansSpringXmlGenerator;
import com.coremedia.beanmodeller.utils.BeanModellerHelper;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.FileCodeWriter;
import org.apache.maven.model.Resource;
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
public class GenerateAccessorizorBeansMojo extends AbstractBeanModellerMojo {

  private static final String SPRING_BEAN_CONFIG_DEFAULT_ROOT_PARENT = "";
  private static final String SPRING_BEAN_NAME_PREFIX = "contentBeanFactory:";

  /**
   * The target name of the generated content bean implementations.
   *
   * @parameter required = true, description = "Target directory for the generated code."
   */
  private String accessorizorBeansTargetPackage;

  /**
   * Where the generated content beans should be saved
   *
   * @parameter default-value="${project.build.directory}/generated-sources/beanmodeller"
   */
  private String accessorizorBeansTargetPath;

  /**
   * Where should the generated spring configuration be saved (base path)
   *
   * @parameter default-value="${project.build.directory}/generated-resources/
   */
  private String springConfigBasePath;

  /**
   * Where the generated bean configuration should be saved
   *
   * @parameter default-value="beanconfig/contentbeans.xml"
   */
  private String springConfigTargetFileName;

  /**
   * Generate content setter methods along with getters.
   *
   * @parameter default-value=false
   */
  private boolean generateSetters;

  private ContentBeanCodeGenerator generator;

  private ContentBeansSpringXmlGenerator configGenerator;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    startTimeMeasurements();

    if (accessorizorBeansTargetPackage == null) {
      throw new MojoFailureException("You must provide a package name for the content beans");
    }

    Set<ContentBeanInformation> roots = analyzeContentBeans();

    getLog().info("Analyzing contentbeans took " + getTimeSinceLastMeasurement() + "ms.");

    generator = new ContentBeanCodeGenerator();
    generator.setGenerateSetters(generateSetters);
    generator.setLog(getLog());
    generator.setPackageName(accessorizorBeansTargetPackage);

    createContentBeanImplementations(roots);
    getLog().info("Creating implementations took " + getTimeSinceLastMeasurement() + "ms.");

    // configure way of xml generation.. freemarker or StringBuilder
    configGenerator = new ContentBeansSpringXmlFreemarkerGenerator();
    configGenerator.setSpringConfigBasePath(springConfigBasePath);
    configGenerator.setSpringConfigTargetFileName(springConfigTargetFileName);
    configGenerator.setCodeGenerator(generator);
    configGenerator.setSpringBeanNamePrefix(SPRING_BEAN_NAME_PREFIX);
    configGenerator.setSpringBeanConfigDefaultRootParent(SPRING_BEAN_CONFIG_DEFAULT_ROOT_PARENT);
    createSpringConfig(roots);

    getLog().info("Creating Spring config took " + getTimeSinceLastMeasurement() + "ms.");
    getLog().info("Total runtime was " + getTimeSinceStart() + "ms.");
  }

  private void createSpringConfig(Set<ContentBeanInformation> roots) throws MojoFailureException {
    // call generator to create file
    configGenerator.createSpringConfig(roots);

    // add resource to project
    Resource resource = new Resource();
    resource.setDirectory(springConfigBasePath);
    resource.addInclude("*/**");
    MavenProject project = getProject();
    if (project != null) {
      project.addResource(resource);
    }
  }

  private void createContentBeanImplementations(Set<ContentBeanInformation> roots) throws MojoFailureException {
    JCodeModel codeModel = generator.generateCode(roots);
    File targetDirectory;
    try {
      targetDirectory = getTargetDirectory();
      getLog().info("Writing bean implementation to directory " + targetDirectory.getPath());
      CodeWriter output = new FileCodeWriter(targetDirectory);
      codeModel.build(output);
    }
    catch (PluginException e) {
      throw new MojoFailureException("There was a problem with the target path", e);
    }
    catch (IOException e) {
      throw new MojoFailureException("Unable to write content bean code", e);
    }
    MavenProject project = getProject();
    //if we are running in a project
    if (project != null) {
      project.addCompileSourceRoot(accessorizorBeansTargetPath);
    }
    else {
      getLog().warn("Not running in a maven project - source will most probably not be compiled!");
    }
  }

  public File getTargetDirectory() throws PluginException {
    File result = null;
    try {
      result = BeanModellerHelper.getSanitizedDirectory(accessorizorBeansTargetPath);
    }
    catch (PluginException e) {
      throw new PluginException("Cannot create target directory", e);
    }
    if (!result.exists()) {
      if (!result.mkdirs()) {
        throw new PluginException("The target path \'" + accessorizorBeansTargetPath + "\' for the generated beans does not exist and cannot be generated");
      }
    }
    if (!result.isDirectory()) {
      throw new PluginException("The target path \'" + accessorizorBeansTargetPath + "\' for the generated beans is no directory");
    }
    if (!result.canWrite()) {
      throw new PluginException("The target path \'" + accessorizorBeansTargetPath + "\' for the generated beans is not writeable");
    }
    return result;
  }
}
