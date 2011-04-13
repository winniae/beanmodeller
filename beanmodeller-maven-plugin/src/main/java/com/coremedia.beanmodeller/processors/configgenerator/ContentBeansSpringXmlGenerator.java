package com.coremedia.beanmodeller.processors.configgenerator;

import com.coremedia.beanmodeller.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.maven.PluginException;
import com.coremedia.beanmodeller.processors.MavenProcessor;
import com.coremedia.beanmodeller.processors.codegenerator.ContentBeanCodeGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * <p/>
 * This class generates the contentbeans.xml
 */
public class ContentBeansSpringXmlGenerator extends MavenProcessor {
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

  public void createSpringConfig(Set<ContentBeanInformation> roots) throws MojoFailureException {
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
      if (getLog().isDebugEnabled()) {
        getLog().debug("Writing spring Configuration for " + contentBeanInformation);
      }
      writer.append(getBeanXml(contentBeanInformation));

      writeBeanConfigRecursive(contentBeanInformation.getChilds(), writer);
    }
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
    //abstract beans do not get a class attribute
    if (!contentBeanInformation.isAbstract()) {
      stringBuilder.append("\t\tclass=\"");
      stringBuilder.append(codeGenerator.getCanonicalGeneratedClassName(contentBeanInformation));
      stringBuilder.append("\"");
    }
    stringBuilder.append("/>\n");
    return stringBuilder.toString();
  }

  private String getBeanName(ContentBeanInformation contentBeanInformation) {
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
