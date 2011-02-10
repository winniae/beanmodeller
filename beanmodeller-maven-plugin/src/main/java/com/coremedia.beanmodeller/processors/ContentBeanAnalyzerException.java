package com.coremedia.beanmodeller.processors;

/**
 * A general Exception that can occur during analyzation. DO NOT USE IT FOR ERRORS IN THE ANALYZATION PROCESS. Use ContentBeanAnalyzationException.
 */
public class ContentBeanAnalyzerException extends Exception {

  public static final String LIFECYCLE_VIOLATION = "You have to analyze the content beans first!";


  public ContentBeanAnalyzerException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public ContentBeanAnalyzerException(String s) {
    super(s);
  }

  public ContentBeanAnalyzerException() {
  }
}
