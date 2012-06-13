package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentBean;

/**
 * 2012/06/13 winfried.mosler@launsch.de
 *
 * Article that extends the standard CBGArticle which modified an external article via doctype aspects.
 */
@ContentBean(doctypeName = "CBGSpecArticle")
public abstract class CBGSpecialArticle extends CBGArticle {

  public abstract String getSpecialSensation();
}
