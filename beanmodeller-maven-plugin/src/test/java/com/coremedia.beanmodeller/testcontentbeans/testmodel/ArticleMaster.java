package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentProperty;

/**
 * 2012/06/21 winfried.mosler@launsch.de
 */
public interface ArticleMaster {

  CBGArticle getMaster();

  /**
   * test if doctypemarshaller only generates this method in Article and not in SpecialArticle
   * SpecialArticle get it via Interface inheritance again, but that should not affect the doctype.xml
   *
   * @return
   */
  Boolean isNice();

  /**
   * String will get longer in SpecialArticle, should set override=true there in doctypes.xml!
   *
   * @return
   */
  @ContentProperty(stringLength = 23)
  String getLonger();
}
