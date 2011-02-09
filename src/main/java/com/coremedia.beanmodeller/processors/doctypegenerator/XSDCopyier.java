package com.coremedia.beanmodeller.processors.doctypegenerator;

import com.coremedia.beanmodeller.processors.MavenProcessor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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

  private String xsdPath;

  public XSDCopyier(String xsdPath) {
    this.xsdPath = xsdPath;
  }

  public void copyXSD(Map<String, URL> schemas) throws DocTypeMarshalerException {
    if (xsdPath == null) {
      throw new DocTypeMarshalerException("You must provide a target path for the XSDs");
    }
    if (schemas == null) {
      throw new DocTypeMarshalerException("You must provide schemas to copy!");
    }
    File targetDir = new File(xsdPath);
    if (!targetDir.exists()) {
      targetDir.mkdirs();
    }
    getLog().info("Copying " + schemas.size() + " schemas to " + xsdPath);
    for (String schemaName : schemas.keySet()) {
      URL schemaUrl = schemas.get(schemaName);
      if (schemaUrl != null && "file".equals(schemaUrl.getProtocol())) {
        getLog().info("Copying " + schemaName + " from " + schemaUrl);
        File sourceFile = new File(schemaUrl.getPath());
        File targetFile = new File(targetDir, sourceFile.getName());
        try {
          FileUtils.copyFile(sourceFile, targetFile);
        }
        catch (IOException e) {
          throw new DocTypeMarshalerException("Unable to copy " + sourceFile + " to " + targetDir);
        }
      }
    }
  }
}
