package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.processors.ContentBeanAnalyzerException;
import com.coremedia.beanmodeller.processors.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshaler;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshalerException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

/**
 * Telekom .COM Relaunch 2011
 * <p> Triggers generation of DocType XML</p>
 *
 * @goal generate-doctypes
 */
public class GenerateDoctypesMojo extends AbstractBeanModellerMojo {

  /**
   * Target path for doctype xml
   *
   * @parameter default-value="${project.build.directory}/doctypes"
   */
  private String docTypeTargetPath;

  /**
   * Filename for doctype XML file
   *
   * @parameter default-value="project-doctypes.xml"
   */
  private String docTypeTargetFileName;

  /**
   * Path for searching abstract content beans.
   *
   * @parameter default-value="."
   */
  private String abstractBeanPath;

  /**
   * Default length for string properties
   *
   * @parameter default-value="32"
   */
  private Integer propertyDefaultStringLength;

  /**
   * Minimal number of items in LinkList
   *
   * @parameter default-value="0"
   */
  private Integer propertyDefaultLinkListMin;

  /**
   * Maximum number of items in LinkList
   *
   * @parameter default-value="1000000"
   */
  private Integer propertyDefaultLinkListMax;

  public void execute() throws MojoExecutionException, MojoFailureException {

    ContentBeanAnalyzator analyzer = new ContentBeanAnalyzator();
    DocTypeMarshaler marshaler = null;

    analyzer.setLog(getLog());
    // searches for annotated abstract content beans in <abstractBeanPath> package 
    analyzer.findContentBeans(abstractBeanPath);

    try {
      analyzer.analyzeContentBeanInformation();
      Set<ContentBeanInformation> rootBeanInformations = analyzer.getContentBeanRoots();
      marshaler = new DocTypeMarshaler(rootBeanInformations);
    }
    catch (ContentBeanAnalyzerException e) {
      getLog().error("Error while running generate-doctypes", e);
      throw new MojoFailureException("Error while running generate-doctypes", e);
    }

    File destFile = null;
    try {
      destFile = getDestinationFile();
    }
    catch (IOException e) {
      getLog().error("Error creating file ", e);
      throw new MojoFailureException("Error creating file", e);
    }

    if (destFile != null) {
      try {
        marshaler.setOutputStream(new FileOutputStream(destFile));
        marshaler.marshallDoctype();
      }
      catch (FileNotFoundException e) {
        getLog().error("Error createting File Output stream! ", e);
        throw new MojoFailureException("Error createting File Output stream! ", e);
      }
      catch (DocTypeMarshalerException e) {
        getLog().error("Error marshalling document model! ", e);
        throw new MojoFailureException("Error marshalling document model! ", e);
      }

    }
  }

  /**
   * <p> Create File object for doctype xml
   *
   * @return
   */
  private File getDestinationFile() throws IOException {
    File destDir = new File(this.docTypeTargetPath);
    if (!destDir.exists()) {
      destDir.mkdir();
    }

    File destFile = new File(destDir, this.docTypeTargetFileName);
    if (!destFile.exists()) {
      destFile.createNewFile();
    }

    return destFile;
  }


}
