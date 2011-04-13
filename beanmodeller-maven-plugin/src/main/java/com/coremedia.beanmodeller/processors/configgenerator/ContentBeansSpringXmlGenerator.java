package com.coremedia.beanmodeller.processors.configgenerator;

import com.coremedia.beanmodeller.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.maven.PluginException;
import com.coremedia.beanmodeller.processors.MavenProcessor;
import com.coremedia.beanmodeller.processors.codegenerator.ContentBeanCodeGenerator;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * <p/>
 * This class generates the contentbeans.xml
 */
public abstract class ContentBeansSpringXmlGenerator extends MavenProcessor {
  private String springConfigTargetFileName;
  private String springConfigBasePath;
  private ContentBeanCodeGenerator codeGenerator;
  private String springBeanConfigDefaultRootParent;
  private String springBeanNamePrefix;

  public String getSpringConfigTargetFileName() {
    return springConfigTargetFileName;
  }

  public void setSpringConfigTargetFileName(String springConfigTargetFileName) {
    this.springConfigTargetFileName = springConfigTargetFileName;
  }

  public String getSpringConfigBasePath() {
    return springConfigBasePath;
  }

  public void setSpringConfigBasePath(String springConfigBasePath) {
    this.springConfigBasePath = springConfigBasePath;
  }

  public ContentBeanCodeGenerator getCodeGenerator() {
    return codeGenerator;
  }

  public void setCodeGenerator(ContentBeanCodeGenerator codeGenerator) {
    this.codeGenerator = codeGenerator;
  }

  public String getSpringBeanConfigDefaultRootParent() {
    return springBeanConfigDefaultRootParent;
  }

  public void setSpringBeanConfigDefaultRootParent(String springBeanConfigDefaultRootParent) {
    this.springBeanConfigDefaultRootParent = springBeanConfigDefaultRootParent;
  }

  public String getSpringBeanNamePrefix() {
    return springBeanNamePrefix;
  }

  public void setSpringBeanNamePrefix(String springBeanNamePrefix) {
    this.springBeanNamePrefix = springBeanNamePrefix;
  }

  abstract public void createSpringConfig(Set<ContentBeanInformation> roots) throws MojoFailureException;

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

  public String getBeanName(ContentBeanInformation contentBeanInformation) {
    if (contentBeanInformation == null) {
      // root node
      return springBeanConfigDefaultRootParent;
    }
    if (contentBeanInformation.isAbstract()) {
      //create a bean which cannot be found by the content bean factory
      return contentBeanInformation.getContentBean().getSimpleName();
    }
    else {
      //create a bean for the conten bean factory
      return springBeanNamePrefix + contentBeanInformation.getDocumentName();
    }
  }
}
