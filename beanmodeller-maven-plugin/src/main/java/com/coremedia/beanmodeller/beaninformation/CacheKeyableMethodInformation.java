package com.coremedia.beanmodeller.beaninformation;

import java.lang.reflect.Method;

/**
 * Store information about methods that should have a CacheKey generated around them.
 */
public class CacheKeyableMethodInformation {

  private final Method method;

  public CacheKeyableMethodInformation(Method method) {

    this.method = method;
  }

  public Method getMethod() {
    return method;
  }
}
