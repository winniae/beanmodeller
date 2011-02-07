package com.coremedia.beanmodeller.processors;

import java.lang.reflect.Method;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * Date: Jan 27, 2011
 * Time: 3:01:55 PM
 */
public abstract class AbstractPropertyInformation implements PropertyInformation {

  /**
   * Provided by constructor. Method object contains all information including origin class of the method.
   */
  private Method method;

  /**
   * Calculated name, set after instantiation. Depends on environment..
   */
  private String documentTypePropertyName;


  public AbstractPropertyInformation(Method method) {
    this.method = method;
  }

  @Override
  public String getDocumentTypePropertyName() {
    return documentTypePropertyName;
  }

  public void setDocumentTypePropertyName(String name) {
    this.documentTypePropertyName = name;
  }

  public Method getMethod() {
    return method;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AbstractPropertyInformation that = (AbstractPropertyInformation) o;

    if (documentTypePropertyName != null ? !documentTypePropertyName.equals(that.documentTypePropertyName) : that.documentTypePropertyName != null) {
      return false;
    }
    if (!method.equals(that.method)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = documentTypePropertyName != null ? documentTypePropertyName.hashCode() : 0;
    result = 31 * result + method.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "PropertyInformation [documentTypePropertyName: " + documentTypePropertyName + "; type: " + getType() + "]";
  }
}
