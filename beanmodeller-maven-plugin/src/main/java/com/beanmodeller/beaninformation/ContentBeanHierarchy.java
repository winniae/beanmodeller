package com.coremedia.beanmodeller.beaninformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class contains all information about the content beans.
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 28.02.11
 * Time: 15:44
 */
public class ContentBeanHierarchy {
  private Set<ContentBeanInformation> rootBeanInformation = new HashSet<ContentBeanInformation>();
  // this hash map is used to have a fast lookup if we already got a bean info for a certain class
  // or to access the found information fastly
  private Map<Class, ContentBeanInformation> allFoundContentBeanInformation = new HashMap<Class, ContentBeanInformation>();


  public Set<ContentBeanInformation> getRootBeanInformation() {
    return rootBeanInformation;
  }

  public Set<? extends ContentBeanInformation> getAllContentBeanInformation() {
    return new HashSet<ContentBeanInformation>(allFoundContentBeanInformation.values());
  }

  public Set<Class> getAllFoundContentBeans() {
    return allFoundContentBeanInformation.keySet();
  }

  public ContentBeanInformation getContentBeanInformation(Class clazz) {
    return allFoundContentBeanInformation.get(clazz);
  }

  public void addContentBeanInformation(Class bean, ContentBeanInformation info) {
    allFoundContentBeanInformation.put(bean, info);
  }
}
