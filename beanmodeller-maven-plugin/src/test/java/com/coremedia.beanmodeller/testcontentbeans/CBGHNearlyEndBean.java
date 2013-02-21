package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;

/**
 */
@ContentBean(doctypeName = "nearlyEnd")
public abstract class CBGHNearlyEndBean extends CBGHMiddleBean{
    public abstract Integer getOtherInt();
}
