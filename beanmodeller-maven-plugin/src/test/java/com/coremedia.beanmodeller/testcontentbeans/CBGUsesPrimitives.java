package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;
import com.coremedia.objectserver.beans.AbstractContentBean;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 02.02.11
 * Time: 12:22
 */
@ContentBean(doctypeName = "primitives")
public abstract class CBGUsesPrimitives extends AbstractContentBean {

  public abstract int getSomeNumber();

  public abstract long getSomeOtherNumber();

  @ContentProperty
  abstract boolean isDecision();

}
