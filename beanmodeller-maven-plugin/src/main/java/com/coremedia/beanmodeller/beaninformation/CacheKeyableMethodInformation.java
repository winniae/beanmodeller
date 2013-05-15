package com.coremedia.beanmodeller.beaninformation;

import java.lang.reflect.Method;

/**
 * Store information about methods that should have a CacheKey generated around them.
 */
public class CacheKeyableMethodInformation {

  private final Method method;
  private final String cacheClass;

  public CacheKeyableMethodInformation(Method method, String cacheClass) {

    this.method = method;
    this.cacheClass = cacheClass;
  }

  public Method getMethod() {
    return method;
  }

  public String getCacheClass() {
    return cacheClass;
  }
}
