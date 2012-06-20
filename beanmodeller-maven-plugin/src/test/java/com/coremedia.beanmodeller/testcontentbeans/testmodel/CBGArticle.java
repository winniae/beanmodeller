package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.testcontentbeans.externalmodel.ExternalArticleImpl;

/**
 * 2012/06/11 winfried.mosler@launsch.de
 * <p/>
 * ContentBean extended from an external article, should generate an doctype aspect.
 * <p/>
 * Must generate accessorizor method for Linkable.
 */
@ContentBean(aspectDoctypeName = "CMArticle")
public abstract class CBGArticle extends ExternalArticleImpl implements Linkable {

  public abstract String getExternalId();
}
