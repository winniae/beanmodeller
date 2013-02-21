package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;

import java.util.List;
import java.util.Map;

/**
 */
@ContentBean
public abstract class CBGHEndBean extends CBGHNearlyEndBean {

    @Override
    public Integer getThisMethodIsNoBeanMethod() {
        return 42;
    }

    @Override
    public Map<String, List> getThisMethodIsNoBeanMethodToo() {
        return null;
    }
}
