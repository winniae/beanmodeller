package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentProperty;

/**
 * 2012/06/21 winfried.mosler@launsch.de
 */
public interface SpecialArticleMaster extends ArticleMaster {

  CBGSpecialArticle getMaster();

  /**
   * String will got longer, should set override=true in doctypes.xml!
   *
   * @return
   */
  @ContentProperty(stringLength = 34)
  String getLonger();
}
