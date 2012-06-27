package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;

/**
 * 2012/06/22 winfried.mosler@launsch.de
 * <p/>
 * Also an Article from the virtual base doctype model we extend here. check if aspect is created correctly
 */
@ContentBean(aspectDoctypeName = "CBGSpecialArt")
public abstract class CBGSpecialAspectArticle extends CBGArticle {


  // override external id with delegation code
  @ContentProperty(propertyName = "externalId")
  protected abstract String getExternalIdInternal();

  @Override
  public String getExternalId() {
    if (getMaster() != null) {
      return getMaster().getExternalId();
    }
    return this.getExternalIdInternal();
  }
}
