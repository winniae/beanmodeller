package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentProperty;
import com.coremedia.cap.common.Blob;

/**
 * 2012/06/13 winfried.mosler@launsch.de
 * <p/>
 * ContentBeans that implement Interfaces may have the Interface's methods generated in Accessorizors.
 */
public interface Linkable extends BasicInterface {

  @ContentProperty(stringLength = 128)
  String getLinkUrl();

  @Override
  Blob getData();
}
