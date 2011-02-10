package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.objectserver.beans.AbstractContentBean;

/**
 * This is not a ContentBean
 */
@ContentBean(doctypeName = "ABC")
public class NotCBGContentBean extends AbstractContentBean {
}
