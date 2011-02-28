package com.coremedia.beanmodeller.processors.beaninformation;

import com.coremedia.cap.common.CapPropertyDescriptorType;

import java.lang.reflect.Method;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * <p/>
 * No special handling.
 */
public class DatePropertyInformation extends AbstractPropertyInformation {

  public DatePropertyInformation(Method method) {
    super(method);
  }

  @Override
  public final CapPropertyDescriptorType getType() {
    return CapPropertyDescriptorType.DATE;
  }
}
