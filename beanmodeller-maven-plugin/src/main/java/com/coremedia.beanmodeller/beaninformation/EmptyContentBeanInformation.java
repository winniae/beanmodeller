package com.coremedia.beanmodeller.beaninformation;

import com.coremedia.objectserver.beans.AbstractContentBean;

import java.util.Collections;
import java.util.Set;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * <p/>
 * Empyt implementation for ContentBeanInformation. May be used as default and/or to compare other CBIs.
 */
public final class EmptyContentBeanInformation implements ContentBeanInformation {

  public static final EmptyContentBeanInformation INSTANCE = new EmptyContentBeanInformation();

  /**
   * Disallow instance creation.
   */
  private EmptyContentBeanInformation() {

  }

  public static EmptyContentBeanInformation getInstance() {
    return INSTANCE;
  }

  @Override
  public ContentBeanInformation getParent() {
    // root object
    return null;
  }

  @Override
  public Set<? extends ContentBeanInformation> getChilds() {
    // no known children
    return Collections.EMPTY_SET;
  }

  @Override
  public String getDocumentName() {
    return "Document";
  }

  @Override
  public String getName() {
    return "Empty";
  }

  @Override
  public Class getContentBean() {
    return AbstractContentBean.class;
  }

  @Override
  public Set<? extends PropertyInformation> getProperties() {
    return Collections.EMPTY_SET;
  }

  @Override
  public String toString() {
    return "EmptyContentBeanInformation";
  }

  @Override
  public boolean isAbstract() {
    return false;
  }
}
