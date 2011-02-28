package com.coremedia.beanmodeller.processors.beaninformation;

import com.coremedia.cap.common.CapPropertyDescriptorType;

import java.lang.reflect.Method;

/**
 * All neccessary informations for a Blob property. Despite the default stuff it is just the allowed mimetypes
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 14.02.11
 * Time: 16:31
 */
public class BlobPropertyInformation extends AbstractPropertyInformation {

  private String allowedMimeTypes;

  public BlobPropertyInformation(Method method) {
    super(method);
  }

  @Override
  public CapPropertyDescriptorType getType() {
    return CapPropertyDescriptorType.BLOB;
  }

  public String getAllowedMimeTypes() {
    return allowedMimeTypes;
  }

  public void setAllowedMimeTypes(String allowedMimeTypes) {
    this.allowedMimeTypes = allowedMimeTypes;
  }
}
