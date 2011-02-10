package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentProperty;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 01.02.11
 * Time: 11:03
 */
public abstract class CBGThisIsNotButShouldBeAContentBean extends CBGContent {

  @ContentProperty(propertyName = "whatTheHell")
  public abstract Integer whatTheHellDoesAPropertyDoHere();
}
