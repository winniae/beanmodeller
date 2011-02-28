package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.processors.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.codegenerator.ContentBeanCodeGenerator;
import com.coremedia.beanmodeller.utils.BeanModellerHelper;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.FileCodeWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

  private ContentBeanCodeGenerator generator;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    startTimeMeasurements();

    if (accessorizorBeansTargetPackage == null) {
      throw new MojoFailureException("You must provide a package name for the content beans");
    }

    Set<ContentBeanInformation> roots = analyzeContentBeans();

    getLog().info("Analyzing contentbeans took " + getTimeSinceLastMeasurement() + "ms.");

    generator = new ContentBeanCodeGenerator();
    generator.setPackageName(accessorizorBeansTargetPackage);

    createContentBeanImplementations(roots);
    getLog().info("Creating implementations took " + getTimeSinceLastMeasurement() + "ms.");

    createSpringConfig(roots);
    getLog().info("Creating Spring config took " + getTimeSinceLastMeasurement() + "ms.");
    getLog().info("Total runtime was " + getTimeSinceStart() + "ms.");
  }

  private void createSpringConfig(Set<ContentBeanInformation> roots) throws MojoFailureException {
    try {
      final File output = getTargetSpringConfigFile();
      final FileWriter writer = new FileWriter(output);

      writer.append("<beans xmlns=\"http://www.springframework.org/schema/beans\"\n");
      writer.append("\t\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
      writer.append("\t\txmlns:util=\"http://www.springframework.org/schema/util\"\n");
      writer.append("\t\txsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\n");
      writer.append("\t\thttp://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-3.0.xsd\">\n");

      writeBeanConfigRecursive(roots, writer);

      writer.append("</beans>");

      writer.flush();
      writer.close();
      Resource resource = new Resource();
      resource.setDirectory(springConfigBasePath);
      resource.addInclude("*/**");
      MavenProject project = getProject();
      if (project != null) {
        project.addResource(resource);
      }
    }
    catch (PluginException e) {
      throw new MojoFailureException("There was a problem with the target file " + springConfigTargetFileName, e);
    }
    catch (IOException e) {
      throw new MojoFailureException("There was a problem with the target file " + springConfigTargetFileName, e);
    }
  }

  private void writeBeanConfigRecursive(Set<? extends ContentBeanInformation> contentBeanInformations, FileWriter writer) throws IOException {
    // sort beans
    SortedSet<ContentBeanInformation> beanInformationsSorted = new TreeSet<ContentBeanInformation>(
        new Comparator<ContentBeanInformation>() {
          @Override
          public int compare(ContentBeanInformation o1, ContentBeanInformation o2) {
            return o1.getDocumentName().compareTo(o2.getDocumentName());
          }
        });
    beanInformationsSorted.addAll(contentBeanInformations);

    // for each bean, write code and call recursive
    for (ContentBeanInformation contentBeanInformation : beanInformationsSorted) {
      writer.append(getBeanXml(contentBeanInformation));

      writeBeanConfigRecursive(contentBeanInformation.getChilds(), writer);
    }
  }

  private void createContentBeanImplementations(Set<ContentBeanInformation> roots) throws MojoFailureException {
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

  public File getTargetSpringConfigFile() throws PluginException, IOException {
    File result = new File(springConfigBasePath, springConfigTargetFileName);
    if (!result.exists()) {
      final File parentFile = result.getParentFile();

      if (!parentFile.exists()) {
        // try to create folder
        if (!parentFile.mkdirs()) {
          throw new PluginException("The folder hierarchy \'" + parentFile + "\' could not be created for the config.");
        }
      }

      if (!result.createNewFile()) {
        throw new PluginException("The target file \'" + springConfigTargetFileName + "\' for the config does not exist");
      }
    }
    if (result.isDirectory()) {
      throw new PluginException("The target file \'" + springConfigTargetFileName + "\' for the config is a directory");
    }
    if (!result.canWrite()) {
      throw new PluginException("The target file \'" + springConfigTargetFileName + "\' for the config is not writeable");
    }
    return result;
  }

  private String getBeanXml(ContentBeanInformation contentBeanInformation) {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("\t<bean name=\"").append(getBeanName(contentBeanInformation)).append("\"\n");
    String beanName = getBeanName(contentBeanInformation.getParent());
    if (!StringUtils.isEmpty(beanName)) {
      stringBuilder.append("\t\tparent=\"").append(beanName).append("\"\n");
    }
    stringBuilder.append("\t\tscope=\"prototype\"\n");
    stringBuilder.append("\t\tclass=\"").append(generator.getCanonicalGeneratedClassName(contentBeanInformation)).append("\"");
    stringBuilder.append("/>\n");

    return stringBuilder.toString();
  }

  private String getBeanName(ContentBeanInformation contentBeanInformation) {
    if (contentBeanInformation == null) {
      // root node
      return SPRING_BEAN_CONFIG_DEFAULT_ROOT_PARENT;
    }

    return SPRING_BEAN_NAME_PREFIX + contentBeanInformation.getDocumentName();
  }
}
