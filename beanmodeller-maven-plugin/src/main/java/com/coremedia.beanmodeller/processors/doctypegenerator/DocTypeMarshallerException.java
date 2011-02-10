package com.coremedia.beanmodeller.processors.doctypegenerator;

/**
 * Telekom .COM Relaunch 2011
 * User: aratas
 * Date: 01.02.2011
 * Time: 12:38:46
 */
public class DocTypeMarshallerException extends Exception {

  public static final String ERROR_MARSHALING = "Error marshaling document types!";

  public DocTypeMarshallerException(String s) {
    super(s);
  }

  public DocTypeMarshallerException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
