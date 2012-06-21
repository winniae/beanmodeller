package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.objectserver.beans.ContentBean;

/**
 * 2012/06/20 winfried.mosler@launsch.de
 * <p/>
 * just implement some method and test interface inheritance
 * <p/>
 * ContentBean introduces methods that are already implemented by AbstractContentBean!
 */
public interface BasicInterface extends ContentBean {

  Boolean isDisabled();

  /**
   * Object is not a valid return type. But the extending interface will overwrite it and BeanModeller should use
   * that return type instead.
   * @return
   */
  Object getData();
}
