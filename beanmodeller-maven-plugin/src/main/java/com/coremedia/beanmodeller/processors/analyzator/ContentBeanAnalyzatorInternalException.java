package com.coremedia.beanmodeller.processors.analyzator;

/**
 * A general Exception that can occur during analyzation. DO NOT USE IT FOR ERRORS IN THE ANALYZATION PROCESS. Use ContentBeanAnalyzationException.
 */
public class ContentBeanAnalyzatorInternalException extends Exception {

  public static final String LIFECYCLE_VIOLATION = "You have to analyze the content beans first!";


  public ContentBeanAnalyzatorInternalException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public ContentBeanAnalyzatorInternalException(String s) {
    super(s);
  }

  public ContentBeanAnalyzatorInternalException() {
  }
}
