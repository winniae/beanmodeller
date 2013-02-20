package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;
import com.coremedia.objectserver.beans.AbstractContentBean;

import java.net.URL;
import java.util.Map;

/**
 * User: marcus
 * Date: 20.02.13
 * Time: 18:00
 */
@ContentBean(doctypeName = "invalidobjects")
public abstract class CBGUsesInvalidObjects extends AbstractContentBean {

    @ContentProperty
    public abstract Map getMap();

    public abstract URL getURL();

}
