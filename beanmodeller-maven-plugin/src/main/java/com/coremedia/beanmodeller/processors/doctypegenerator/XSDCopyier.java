package com.coremedia.beanmodeller.processors.doctypegenerator;

import com.coremedia.beanmodeller.beaninformation.GrammarInformation;
import com.coremedia.beanmodeller.maven.PluginException;
import com.coremedia.beanmodeller.processors.MavenProcessor;
import com.coremedia.beanmodeller.utils.BeanModellerHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * This class copies all XSD found by the marshaller to the path 'lib/xml' of the content server
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 09.02.11
 * Time: 11:51
 */
public class XSDCopyier extends MavenProcessor {

  private static final String CLASSPATH_DENOMITOR = "classpath:";
  private String xsdPath;

  public XSDCopyier(String xsdPath) {
    this.xsdPath = xsdPath;
  }

  public void copyXSD(Map<String, GrammarInformation> schemas) throws DocTypeMarshallerException {
    if (xsdPath == null) {
      throw new DocTypeMarshallerException("You must provide a target path for the XSDs");
    }
    if (schemas == null) {
      throw new DocTypeMarshallerException("You must provide schemas to copy!");
    }
    File targetDir = null;
    try {
      targetDir = BeanModellerHelper.getSanitizedDirectory(xsdPath);
    }
    catch (PluginException e) {
      throw new DocTypeMarshallerException("Unable to get target directory", e);
    }
    getLog().info("Copying " + schemas.size() + " schemas to " + xsdPath);
    for (String schemaName : schemas.keySet()) {
      copySchema(schemas, targetDir, schemaName);
    }
  }

  private void copySchema(Map<String, GrammarInformation> schemas, File targetDir, String schemaName) throws DocTypeMarshallerException {
    GrammarInformation grammarInformation = schemas.get(schemaName);
    for (String schemaLocation : grammarInformation.getGrammarLocations()) {
      URL schemaUrl = null;
      if (schemaLocation.startsWith(CLASSPATH_DENOMITOR)) {
        String grammarSource = schemaLocation.substring(CLASSPATH_DENOMITOR.length());
        // get URL just like com.coremedia.io.ResourceLoader (line 119) gets it
        schemaUrl = Thread.currentThread().getContextClassLoader().getResource(grammarSource);
      }

      if (schemaUrl != null && ("file".equals(schemaUrl.getProtocol()) || "jar".equals(schemaUrl.getProtocol()))) {
        try {
          String targetFileName = getTargetFileName(schemaName, schemaLocation);
          File targetFile = BeanModellerHelper.getSanitizedFile(targetDir, targetFileName);
          if ("file".equals(schemaUrl.getProtocol())) {
            copyFile(schemaName, schemaUrl, targetFile);
          }
          else if ("jar".equals(schemaUrl.getProtocol())) {
            copyJarResource(schemaName, schemaUrl, targetFile);
          }
        }
        catch (IOException e) {
          throw new DocTypeMarshallerException("Unable to copy " + schemaUrl + " to " + targetDir, e);
        }
        catch (PluginException e) {
          throw new DocTypeMarshallerException("Unable to copy " + schemaUrl + " to " + targetDir, e);
        }
      }
      else {
        if (schemaUrl == null) {
          getLog().warn("Unable to copy the schema since the URL is null!");
        }
        else {
          getLog().warn("Unable to copy " + schemaUrl + " since I cannot handle protocol " + schemaUrl.getProtocol() + "!");
        }
      }
    }
  }

  private String getTargetFileName(String schemaName, String schemaLocation) {
    String targetFileName;
    if (schemaLocation != null && schemaLocation.startsWith(CLASSPATH_DENOMITOR)) {
      targetFileName = schemaLocation.substring(CLASSPATH_DENOMITOR.length());
    }
    else {
      targetFileName = schemaName;
    }
    return targetFileName;
  }

  private void copyJarResource(String schemaName, URL schemaUrl, File targetFile) throws DocTypeMarshallerException, IOException {
    String resourcePath = schemaUrl.getPath();
    int resourceNamePosition = resourcePath.lastIndexOf('!');
    if (resourceNamePosition < 0) {
      throw new DocTypeMarshallerException("Unable to determine filename from " + schemaUrl + ".");
    }
    String resourceName = resourcePath.substring(resourceNamePosition + 1);
    getLog().info("Copying " + schemaName + " from classpath " + resourceName + "(" + schemaUrl + ") to " + targetFile.getAbsolutePath());
    InputStream resourceStream = XSDCopyier.class.getResourceAsStream(resourceName);
    if (resourceStream == null) {
      throw new DocTypeMarshallerException("Unable to open input stream for resource " + resourceName + " from URL " + schemaUrl);
    }
    FileUtils.copyInputStreamToFile(resourceStream, targetFile);
  }

  private void copyFile(String schemaName, URL schemaUrl, File targetFile) throws DocTypeMarshallerException, IOException {
    File sourceFile = new File(schemaUrl.getPath());
    if (sourceFile.length() == 0) {
      throw new DocTypeMarshallerException("Unable to read " + sourceFile);
    }
    getLog().info("Copying " + schemaName + " from " + sourceFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
    FileUtils.copyFile(sourceFile, targetFile);
  }
}
