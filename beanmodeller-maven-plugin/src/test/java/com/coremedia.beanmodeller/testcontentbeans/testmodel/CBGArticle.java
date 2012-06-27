package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;
import com.coremedia.beanmodeller.testcontentbeans.externalmodel.ExternalArticleImpl;

/**
 * 2012/06/11 winfried.mosler@launsch.de
 * <p/>
 * ContentBean extended from an external article, should generate an doctype aspect.
 * <p/>
 * Must generate accessorizor method for Linkable.
 */
@ContentBean(aspectDoctypeName = "CMArticle")
public abstract class CBGArticle<T extends CBGArticle> extends ExternalArticleImpl implements Linkable, ArticleMaster {

  public abstract String getExternalId();

  // in override situations, different properties have different jaxb behavior..
  public abstract Integer getExtIdInt();


  // make a method that is supposed to be casted to appropriate supertype, type is back-injected via generics...
  // the catch comes with the Accessorizors that have to understand and generate generics code now
  @ContentProperty(propertyName = "master")
  protected abstract CBGArticle getMasterInternal();


  @Override
  public T getMaster() {
    return (T) getMasterInternal();
  }
}
