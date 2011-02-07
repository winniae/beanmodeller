package com.coremedia.beanmodeller.processors.doctypegenerator;

/**
 * Telekom .COM Relaunch 2011
 * User: aratas
 * Date: 01.02.2011
 * Time: 12:38:46
 */
public class DocTypeMarshalerException extends Exception {

  public static final String ERROR_MARSHALING = "Error marshaling document types!";

  public DocTypeMarshalerException(String s) {
    super(s);
  }

}
