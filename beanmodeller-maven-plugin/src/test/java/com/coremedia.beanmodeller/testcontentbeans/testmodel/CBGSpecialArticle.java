package com.coremedia.beanmodeller.testcontentbeans.testmodel;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;

import java.util.List;

/**
 * 2012/06/13 winfried.mosler@launsch.de
 * <p/>
 * Article that extends the standard CBGArticle which modified an external article via doctype aspects.
 */
@ContentBean(doctypeName = "CBGSpecArticle")
public abstract class CBGSpecialArticle<T extends CBGSpecialArticle> extends CBGAlmostSpecialArticle<T> implements SpecialArticleMaster {

  public abstract String getSpecialSensation();

  /**
   * implement this method only here, so when AlmostSpecialArticle is analyzed, it might fail
   *
   * @return
   */
  @Override
  public Object getNotFromContent() {
    return null;
  }

  /**
   * BeanModeller must handle WildCardTypes, too.. should generate type of CBGImage
   *
   * @return
   */
  public abstract List<? extends CBGImage> getImages();


  // override external id with delegation code
  @ContentProperty(propertyName = "extIdInt")
  protected abstract Integer getExtIdInternal();

  @Override
  public Integer getExtIdInt() {
    if (getMaster() != null) {
      return getMaster().getExtIdInt();
    }
    return this.getExtIdInternal();
  }
}
