package com.coremedia.beanmodeller.utils;

import com.coremedia.beanmodeller.maven.PluginException;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

/**
 * Telekom .COM Relaunch 2011
 * User: aratas
 * Date: 10.02.2011
 * Time: 10:38:04
 */
public class BeanModellerHelper {
  public static final String ERROR_CREATING_TARGET_DIRECTORY = "Target directory could not be created! ";

  public static File getSanitizedFile(String directory, String fileName) throws PluginException  {
    File destDir = getSanitizedDirectory(directory);
    File targetFile = new File(destDir,fileName);
    return targetFile;
  }

  public static File getSanitizedDirectory(String directory) throws PluginException {
    String dirName = FilenameUtils.separatorsToSystem(directory);
    File destDir = new File(dirName);
    if (!destDir.exists() && !destDir.mkdirs()) {
      // target directory does not exist and could not be created
      throw new PluginException(ERROR_CREATING_TARGET_DIRECTORY + ": " + directory, new RuntimeException(ERROR_CREATING_TARGET_DIRECTORY));
    }
    return destDir;
  }
}
