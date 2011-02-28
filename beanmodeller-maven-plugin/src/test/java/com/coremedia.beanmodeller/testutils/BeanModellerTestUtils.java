package com.coremedia.beanmodeller.testutils;

import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzatorInternalException;
import com.coremedia.beanmodeller.processors.beaninformation.ContentBeanInformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Some static methods to make the test methods shorter.
 */
public class BeanModellerTestUtils {

  public static boolean analyzationErrorContainsMessage(ContentBeanAnalyzationException exception, String message) {
    List<ContentBeanAnalyzationException.ContentBeanAnalyzationError> errors = exception.getErrors();

    for (ContentBeanAnalyzationException.ContentBeanAnalyzationError error : errors) {
      String errorMessage = error.getMessage();
      if (errorMessage.contains(message)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns all found content beans (or documents) as a Map with DocumentName of the resulting ContetnBean as the Key Value of the Map.
   *
   * @return All ContentBean-Informations as Map.
   * @throws com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzatorInternalException
   *          when analyzation failed.
   */
  public static Map<String, ContentBeanInformation> getContentBeans(Set<ContentBeanInformation> rootBeanInformation) throws ContentBeanAnalyzatorInternalException {
    if (rootBeanInformation == null) {
      throw new ContentBeanAnalyzatorInternalException(ContentBeanAnalyzationException.LIFECYCLE_VIOLATION);
    }
    // flatten tree hierarchy
    return getContentBeansRecursive(rootBeanInformation);
  }

  private static Map<String, ContentBeanInformation> getContentBeansRecursive(Set<? extends ContentBeanInformation> contentBeanInformations) {
    Map<String, ContentBeanInformation> beanInformationMap = new HashMap<String, ContentBeanInformation>();

    for (ContentBeanInformation information : contentBeanInformations) {
      // add provided CBI to map
      beanInformationMap.put(information.getDocumentName(), information);

      // call method recursively for all children
      beanInformationMap.putAll(getContentBeansRecursive(information.getChilds()));
    }

    return beanInformationMap;
  }
}
