package com.coremedia.beanmodeller.processors.configgenerator;

import com.coremedia.beanmodeller.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.maven.PluginException;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.SortedSet;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * <p/>
 * This class generates the contentbeans.xml
 */
public class ContentBeansSpringXmlStringBuilderGenerator extends ContentBeansSpringXmlGenerator {
  public void createSpringConfig(Set<ContentBeanInformation> roots) throws MojoFailureException {
    try {
      final Writer writer = getTargetSpringConfigFile();

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
      throw new MojoFailureException("There was a problem with the target file " + getSpringConfigTargetFileName(), e);
    }
    catch (IOException e) {
      throw new MojoFailureException("There was a problem with the target file " + getSpringConfigTargetFileName(), e);
    }
  }

  private void writeBeanConfigRecursive(Set<? extends ContentBeanInformation> contentBeanInformations, Writer writer) throws IOException {
    SortedSet<ContentBeanInformation> beanInformationsSorted = sortContentBeanInformationSet(contentBeanInformations);

    // for each bean, write code and call recursive
    for (ContentBeanInformation contentBeanInformation : beanInformationsSorted) {
      if (getLog().isDebugEnabled()) {
        getLog().debug("Writing spring Configuration for " + contentBeanInformation);
      }
      writer.append(getBeanXml(contentBeanInformation));

      writeBeanConfigRecursive(contentBeanInformation.getChilds(), writer);
    }
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
      stringBuilder.append(getCodeGenerator().getCanonicalGeneratedClassName(contentBeanInformation));
      stringBuilder.append("\"");
    }
    stringBuilder.append("/>\n");
    return stringBuilder.toString();
  }
}
