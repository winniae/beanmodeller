package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;

/**
 */
@ContentBean
public abstract class CBGHEndBean extends CBGHNearlyEndBean {

    @Override
    public Integer thisMethodIsNoBeanMethod() {
        return 42;
    }
}
