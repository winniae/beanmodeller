package com.coremedia.beanmodeller.processors.beaninformation;

import com.coremedia.cap.common.CapPropertyDescriptorType;

import java.lang.reflect.Method;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * Date: Jan 27, 2011
 * Time: 2:57:35 PM
 * <p/>
 * Abstract Information on different Property Types.
 */
public interface PropertyInformation {

  /**
   * The name of the property, shortened to be document type compatible.
   *
   * @return Name of the property.
   */
  String getDocumentTypePropertyName();

  /**
   * Every property has a type. Type comes from CoreMedia Core component objectserver.
   *
   * @return Type of the property.
   */
  CapPropertyDescriptorType getType();

  /**
   * The original Method represented by this property.
   *
   * @return the method represented by this property.
   */
  Method getMethod();
}
