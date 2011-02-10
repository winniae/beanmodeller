package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 01.02.11
 * Time: 11:02
 */
@ContentBean(doctypeName = "CBGCHierarchy")
public abstract class CBGContentBeanInWreckedHierarchy extends CBGThisIsNotButShouldBeAContentBean {
  public abstract Integer someIntProperty();

  @ContentProperty
  public Integer thisPropertyIsNoProperty() {
    return 0;
  }
}
