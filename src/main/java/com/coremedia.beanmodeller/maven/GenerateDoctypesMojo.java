package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.processors.ContentBeanAnalyzerException;
import com.coremedia.beanmodeller.processors.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshalerException;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshaller;
import com.coremedia.beanmodeller.processors.doctypegenerator.XSDCopyier;
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

  /**
   * Where should the custom XML Schema definitions copied.
   *
   * @parameter default-value="${project.build.directory}/contentserver/lib/xml", required=true, description="Where should the xml schemas be copied"
   */
  private String xsdTargetDir;

  public static final String ERROR_CREATING_TARGET_DIRECTORY = "Target directory could not be created! ";
  public static final String ERROR_CREATING_TARGET_FILE = "Error creating file! ";
  public static final String ERROR_GENERATING_DOCTYPES = "Error while running generate-doctypes! ";

  public void execute() throws MojoExecutionException, MojoFailureException {

    ContentBeanAnalyzator analyzer = new ContentBeanAnalyzator();
    DocTypeMarshaller marshaller = null;

    analyzer.setLog(getLog());
    // searches for annotated abstract content beans in <abstractBeanPath> package 
    analyzer.findContentBeans(abstractBeanPath);

    Set<ContentBeanInformation> rootBeanInformations;
    try {
      analyzer.analyzeContentBeanInformation();
      rootBeanInformations = analyzer.getContentBeanRoots();
    }
    catch (ContentBeanAnalyzerException e) {
      getLog().error(ERROR_GENERATING_DOCTYPES, e);
      throw new MojoFailureException(ERROR_GENERATING_DOCTYPES, e);
    }

    File destFile = getDestinationFile();

    if (destFile != null) {
      getLog().info("Writing doctype XML to " + destFile.getPath());
      try {
        OutputStream doctypeXMLOutputStream = new FileOutputStream(destFile);
        marshaller = new DocTypeMarshaller(rootBeanInformations, doctypeXMLOutputStream);
        marshaller.setLog(getLog());
        marshaller.marshallDoctype();
      }
      catch (FileNotFoundException e) {
        getLog().error("Error createting File Output stream! ", e);
        throw new MojoFailureException("Error creating File Output stream! ", e);
      }
      catch (DocTypeMarshalerException e) {
        getLog().error("Error marshalling document model! ", e);
        throw new MojoFailureException("Error marshaling document model! ", e);
      }

      //copy the xsd
      XSDCopyier copyier = new XSDCopyier(xsdTargetDir);
      copyier.setLog(getLog());
      try {
        copyier.copyXSD(marshaller.getFoundMarkupSchemaDefinitions());
      }
      catch (DocTypeMarshalerException e) {
        throw new MojoFailureException("Unable to copy the XSD", e);
      }
    }
  }

  /**
   * <p> Create File object for doctype xml
   *
   * @return
   */
  private File getDestinationFile() throws MojoFailureException {
    File destDir = new File(this.docTypeTargetPath);
    if (!destDir.exists() && !destDir.mkdir()) {
      // target directory does not exist and could not be created
      getLog().error(ERROR_CREATING_TARGET_DIRECTORY + ": " + this.docTypeTargetPath);
      throw new MojoFailureException(ERROR_CREATING_TARGET_DIRECTORY, new RuntimeException(ERROR_CREATING_TARGET_DIRECTORY));
    }

    File destFile = new File(destDir, this.docTypeTargetFileName);
    if (!destFile.exists()) {
      try {
        destFile.createNewFile();
      }
      catch (IOException e) {
        getLog().error(ERROR_CREATING_TARGET_FILE + ": " + this.docTypeTargetFileName, e);
        throw new MojoFailureException(ERROR_CREATING_TARGET_FILE, e);
      }
    }

    return destFile;
  }

}
