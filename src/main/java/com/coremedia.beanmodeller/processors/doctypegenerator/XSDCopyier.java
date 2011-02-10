package com.coremedia.beanmodeller.processors.doctypegenerator;

import com.coremedia.beanmodeller.maven.PluginException;
import com.coremedia.beanmodeller.processors.MavenProcessor;
import com.coremedia.beanmodeller.utils.BeanModellerHelper;
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

  public void copyXSD(Map<String, URL> schemas) throws DocTypeMarshallerException {
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
      URL schemaUrl = schemas.get(schemaName);
      if (schemaUrl != null && "file".equals(schemaUrl.getProtocol())) {
        getLog().info("Copying " + schemaName + " from " + schemaUrl);
        File sourceFile = new File(schemaUrl.getPath());
        try {
          File targetFile = BeanModellerHelper.getSanitizedFile(targetDir, sourceFile.getName());
          FileUtils.copyFile(sourceFile, targetFile);
        }
        catch (IOException e) {
          throw new DocTypeMarshallerException("Unable to copy " + sourceFile + " to " + targetDir, e);
        }
        catch (PluginException e) {
          throw new DocTypeMarshallerException("Unable to copy " + sourceFile + " to " + targetDir, e);
        }
      }
      else {
        getLog().warn("Unable to copy " + schemaUrl + " since I do not know how!");
      }
    }
  }
}
