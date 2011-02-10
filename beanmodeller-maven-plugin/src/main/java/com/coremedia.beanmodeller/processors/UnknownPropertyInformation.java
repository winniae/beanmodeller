package com.coremedia.beanmodeller.processors;

import com.coremedia.cap.common.CapPropertyDescriptorType;

import java.lang.reflect.Method;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * Date: Jan 28, 2011
 * Time: 3:27:24 PM
 */
public class UnknownPropertyInformation extends AbstractPropertyInformation {

  public UnknownPropertyInformation(Method method) {
    super(method);
  }

  @Override
  public CapPropertyDescriptorType getType() {
    // type for unknown property type
    return CapPropertyDescriptorType.CAP_STRUCT; // todo probably the wrong type ...
  }
}
