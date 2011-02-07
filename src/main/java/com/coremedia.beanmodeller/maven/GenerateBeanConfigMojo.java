package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.processors.ContentBeanInformation;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 *
 * @goal generateBeanConfig
 */
public class GenerateBeanConfigMojo extends AbstractBeanModellerMojo {

  /**
   * Where the generated bean configuration should be saved
   *
   * @parameter default-value="${project.build.directory}/resource/contentbeans.xml"
   */
  private String targetFile;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    Set<ContentBeanInformation> roots = analyzeContentBeans();

    // Create contentbeans xml, write to targetFile

    try {
      final File output = getTargetFile();
      final FileWriter writer = new FileWriter(output);

      writer.append("<beans>");
      for (ContentBeanInformation contentBeanInformation : roots) {
        writer.append(getBeanXml(contentBeanInformation));
      }
      writer.append("</beans>");

      writer.flush();
      writer.close();
    }
    catch (PluginException e) {
      throw new MojoFailureException("There was a problem with the target file " + targetFile, e);
    }
    catch (IOException e) {
      throw new MojoFailureException("There was a problem with the target file " + targetFile, e);
    }
  }


  public File getTargetFile() throws PluginException {
    File result = new File(targetFile);
    if (!result.exists()) {
      throw new PluginException("The target file \'" + targetFile + "\' for the config does not exist");
    }
    if (result.isDirectory()) {
      throw new PluginException("The target file \'" + targetFile + "\' for the config is a directory");
    }
    if (!result.canWrite()) {
      throw new PluginException("The target file \'" + targetFile + "\' for the config is not writeable");
    }
    return result;
  }

  private String getBeanXml(ContentBeanInformation contentBeanInformation) {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("<bean\n");
    stringBuilder.append("name=\"contentBeanFactory:").append(contentBeanInformation.getDocumentName()).append("\"\n");
    stringBuilder.append("parent=\"").append(contentBeanInformation.getParent().getDocumentName()).append("\"\n");
    stringBuilder.append("scope=\"prototype\"\n");
    stringBuilder.append("class=\"").append("\"\n");
    stringBuilder.append("/>\n");

    return stringBuilder.toString();
  }
}
