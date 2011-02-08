package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.processors.ContentBeanAnalyzerException;
import com.coremedia.beanmodeller.processors.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshaler;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshalerException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

  public static final String ERROR_CREATING_TARGET_DIRECTORY = "Target directory could not be created! ";
  public static final String ERROR_CREATING_TARGET_FILE = "Error creating file! ";
  public static final String ERROR_GENERATING_DOCTYPES = "Error while running generate-doctypes! ";

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
      getLog().error(ERROR_GENERATING_DOCTYPES, e);
      throw new MojoFailureException(ERROR_GENERATING_DOCTYPES, e);
    }

    File destFile = null;
    try {
      destFile = getDestinationFile();
    }
    catch (IOException e) {
      getLog().error(ERROR_CREATING_TARGET_FILE, e);
      throw new MojoFailureException(ERROR_CREATING_TARGET_FILE, e);
    }

    if (destFile != null) {
      try {
        OutputStream os = new FileOutputStream(destFile);
        marshaler.setOutputStream(os);
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
  private File getDestinationFile() throws MojoFailureException, IOException {
    File destDir = new File(this.docTypeTargetPath);
    if (!destDir.exists() && !destDir.mkdir()) {
      // target directory does not exist and could not be created
      throw new MojoFailureException(ERROR_CREATING_TARGET_DIRECTORY);
    }

    File destFile = new File(destDir, this.docTypeTargetFileName);
    if (!destFile.exists()) {
      destFile.createNewFile();
    }

    return destFile;
  }

  public String getDocTypeTargetPath() {
    return docTypeTargetPath;
  }

  public void setDocTypeTargetPath(String docTypeTargetPath) {
    this.docTypeTargetPath = docTypeTargetPath;
  }

  public String getDocTypeTargetFileName() {
    return docTypeTargetFileName;
  }

  public void setDocTypeTargetFileName(String docTypeTargetFileName) {
    this.docTypeTargetFileName = docTypeTargetFileName;
  }

  public String getAbstractBeanPath() {
    return abstractBeanPath;
  }

  public void setAbstractBeanPath(String abstractBeanPath) {
    this.abstractBeanPath = abstractBeanPath;
  }

  public Integer getPropertyDefaultStringLength() {
    return propertyDefaultStringLength;
  }

  public void setPropertyDefaultStringLength(Integer propertyDefaultStringLength) {
    this.propertyDefaultStringLength = propertyDefaultStringLength;
  }

  public Integer getPropertyDefaultLinkListMin() {
    return propertyDefaultLinkListMin;
  }

  public void setPropertyDefaultLinkListMin(Integer propertyDefaultLinkListMin) {
    this.propertyDefaultLinkListMin = propertyDefaultLinkListMin;
  }

  public Integer getPropertyDefaultLinkListMax() {
    return propertyDefaultLinkListMax;
  }

  public void setPropertyDefaultLinkListMax(Integer propertyDefaultLinkListMax) {
    this.propertyDefaultLinkListMax = propertyDefaultLinkListMax;
  }
}
