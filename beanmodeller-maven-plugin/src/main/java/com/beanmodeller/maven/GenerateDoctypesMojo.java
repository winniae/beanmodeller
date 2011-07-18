package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshaller;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshallerException;
import com.coremedia.beanmodeller.processors.doctypegenerator.XSDCopyier;
import com.coremedia.beanmodeller.utils.BeanModellerHelper;
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
 * <p/>
 * Generates a doctypes.xml based on exctracted beaninformation.
 * Copies required xsd files.
 *
 * @goal generate-doctypes
 */
public class GenerateDoctypesMojo extends AbstractBeanModellerMojo {

  /**
   * Target path for doctype xml
   *
   * @parameter default-value="${project.build.directory}/${project.build.finalName}/config/contentserver/doctypes"
   */
  private String docTypeTargetPath;

  /**
   * Filename for doctype XML file
   *
   * @parameter default-value="project-doctypes.xml"
   */
  private String docTypeTargetFileName;

  /**
   * Where should the custom XML Schema definitions copied.
   *
   * @parameter default-value="${project.build.directory}/${project.build.finalName}/lib/classes"
   */
  private String xsdTargetPath;

  public static final String ERROR_CREATING_TARGET_FILE = "Error creating file ";

  public void execute() throws MojoExecutionException, MojoFailureException {
    startTimeMeasurements();

    // Exctract beaninformation
    Set<ContentBeanInformation> rootBeanInformations = analyzeContentBeans();
    getLog().info("Analyzing content beans took " + getTimeSinceLastMeasurement() + "ms.");

    // Write doctypes.xml
    File destFile = getDestinationFile();

    DocTypeMarshaller marshaller;
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
      catch (DocTypeMarshallerException e) {
        getLog().error("Error marshalling document model! ", e);
        throw new MojoFailureException("Error marshaling document model! ", e);
      }

      getLog().info("Generating doctype.xml took " + getTimeSinceLastMeasurement() + "ms.");

      // copy the xsd
      XSDCopyier copyier = new XSDCopyier(xsdTargetPath);
      copyier.setLog(getLog());
      try {
        copyier.copyXSD(marshaller.getFoundMarkupSchemaDefinitions());
      }
      catch (DocTypeMarshallerException e) {
        throw new MojoFailureException("Unable to copy the XSD", e);
      }
    }
    getLog().info("Copying XSDs took " + getTimeSinceLastMeasurement() + "ms.");
    getLog().info("Total runtime was " + getTimeSinceStart() + "ms.");
  }

  /**
   * <p> Create File object for doctype xml
   *
   * @return
   */
  private File getDestinationFile() throws MojoFailureException {
    File destFile = null;
    try {
      destFile = BeanModellerHelper.getSanitizedFile(this.docTypeTargetPath, this.docTypeTargetFileName);
    }
    catch (PluginException e) {
      throw new MojoFailureException("Unable to get target file", e);
    }
    if (!destFile.exists()) {
      try {
        destFile.createNewFile();
      }
      catch (IOException e) {
        getLog().error(ERROR_CREATING_TARGET_FILE + ": " + this.docTypeTargetFileName, e);
        throw new MojoFailureException(ERROR_CREATING_TARGET_FILE + e.getMessage(), e);
      }
    }

    return destFile;
  }

}
