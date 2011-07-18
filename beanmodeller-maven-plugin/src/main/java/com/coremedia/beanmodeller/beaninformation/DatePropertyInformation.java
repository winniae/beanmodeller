package com.coremedia.beanmodeller.beaninformation;

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

  @Override
  public String getHumanUnderstandableRepresentation() {
    StringBuilder builder = new StringBuilder();
    builder.append("Date property ");
    builder.append(getDocumentTypePropertyName());
    builder.append(" for ");
    builder.append(getMethod().getName());
    return builder.toString();
  }

  @Override
  public String toString() {
    return "DatePropertyInformation{" +
        "method=" + getMethod() +
        ", documentTypePropertyName='" + getDocumentTypePropertyName() + '\'' +
        '}';
  }
}
