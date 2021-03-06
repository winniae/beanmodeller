package com.coremedia.beanmodeller.beaninformation;

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

  @Override
  public String getHumanUnderstandableRepresentation() {
    StringBuilder builder = new StringBuilder();
    builder.append("Blob property ");
    builder.append(getDocumentTypePropertyName());
    builder.append(" for ");
    builder.append(getMethod().getName());
    builder.append(", allowed MIME types: ");
    builder.append(allowedMimeTypes);
    return builder.toString();
  }

  @Override
  public String toString() {
    return "BlobPropertyInformation{" +
        "method=" + getMethod() +
        ", documentTypePropertyName='" + getDocumentTypePropertyName() + '\'' +
        "allowedMimeTypes='" + allowedMimeTypes + '\'' +
        '}';
  }
}
