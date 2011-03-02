package com.coremedia.beanmodeller.beaninformation;

import com.coremedia.cap.common.CapPropertyDescriptorType;

import java.lang.reflect.Method;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * <p/>
 * String properties have a length attribute.
 */
public class StringPropertyInformation extends AbstractPropertyInformation {

  /**
   * Maximum length of a String property in the content repository.
   */
  private int length;

  public StringPropertyInformation(Method method) {
    super(method);
  }

  @Override
  public final CapPropertyDescriptorType getType() {
    return CapPropertyDescriptorType.STRING;
  }

  public final int getLength() {
    return length;
  }

  public final void setLength(int length) {
    this.length = length;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    StringPropertyInformation that = (StringPropertyInformation) o;

    if (length != that.length) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + length;
    return result;
  }

  @Override
  public String toString() {
    return "PropertyInformation [name: " + getDocumentTypePropertyName() + "; type: " + getType() + "; length: " + getLength() + "]";
  }
}
