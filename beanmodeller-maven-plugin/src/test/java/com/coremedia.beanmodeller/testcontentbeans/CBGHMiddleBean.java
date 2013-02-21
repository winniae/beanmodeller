package com.coremedia.beanmodeller.testcontentbeans;

import java.util.List;
import java.util.Map;

/**
 * a bean in the middle which is no content bean
 */
public abstract class CBGHMiddleBean extends CBGHBaseBean {

    /**
     * a test method for an allowed abstract method
     * @return
     */
    public abstract Integer getThisMethodIsNoBeanMethod();

    public abstract Map<String, List> getThisMethodIsNoBeanMethodToo();
}
