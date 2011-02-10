package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;

/**
 * This bean is used if the analyzator correctly detects that there is allready a CBGContent
 */
@ContentBean(doctypeName = "CBGContent")
public abstract class CBGBeanPretendingBeeingCBGContent extends CBGContent {
}
