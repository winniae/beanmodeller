package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.testcontentbeans.externalmodel.ExternalArticleImpl;

/**
 * 2012/06/11 winfried.mosler@launsch.de
 *
 * ContentBean extended from an external article, should get that as parent in doctype definition.
 */
@ContentBean(parentDoctypeName = "CMArticle")
public abstract class CBGArticle extends ExternalArticleImpl {

  public abstract String getExternalId();
}
