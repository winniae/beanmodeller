package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.objectserver.beans.AbstractContentBean;

/**
 * jsut some base bea
 */
@ContentBean
public abstract class CBGHBaseBean extends AbstractContentBean {
    
    public abstract Integer getInt();
}
