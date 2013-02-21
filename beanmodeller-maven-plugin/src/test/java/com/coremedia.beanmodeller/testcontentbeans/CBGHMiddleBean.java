package com.coremedia.beanmodeller.testcontentbeans;

import java.util.Map;

/**
 * a bean in the middle which is no content bean
 */
public abstract class CBGHMiddleBean extends CBGHBaseBean {

    /**
     * a test method for an allowed abstract method
     * @return
     */
    public abstract Integer thisMethodIsNoBeanMethod();

    public abstract Map thisMethodIsNoBeanMethodToo();
}
