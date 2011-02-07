package com.coremedia.beanmodeller.processors;

import java.util.Set;

/**
 * The main content bean analyzer. It resembles some content bean information from the found content bean classes.
 */
public interface ContentBeanAnalyzer {

  /**
   * Add a content bean for further analysis. This is used by the annotation finder.
   *
   * @param bean the bean that is a content bean
   */
  void addContentBean(Class bean);

  /**
   * Start analyzation process.
   *
   * @return Throws exception if errors occured during analyzation .
   * @throws ContentBeanAnalyzationException
   *          when Errors occur in the analyzation process.
   */
  void analyzeContentBeanInformation() throws ContentBeanAnalyzationException;

  /**
   * Returns all found root content beans (or documents).
   *
   * @return set of root elements
   * @throws ContentBeanAnalyzerException when analyzation failed.
   */
  Set<ContentBeanInformation> getContentBeanRoots() throws ContentBeanAnalyzerException;
}
