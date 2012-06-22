package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentBean;

/**
 * 2012/06/22 winfried.mosler@launsch.de
 *
 * Also an Article from the virtual base doctype model we extend here. check if aspect is created correctly
 */
@ContentBean(aspectDoctypeName = "CBGSpecialArt")
public abstract class CBGSpecialAspectArticle extends CBGArticle {
}
