package com.coremedia.beanmodeller.beaninformation;

import com.coremedia.cap.common.CapPropertyDescriptorType;

import java.lang.reflect.Method;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * <p/>
 * <p/>
 * LinkListProperty links to from min to max documents. The type of the target document ist specified with linktype.
 */
public class LinkListPropertyInformation extends AbstractPropertyInformation {
  /**
   * linkType specifies the type of content which can be referenced by this property.
   */
  private ContentBeanInformation linkType = EmptyContentBeanInformation.getInstance();

  /**
   * Min number of linked elements.
   */
  private int min = 0;

  /**
   * Max number of linked elements.
   */
  private int max = Integer.MAX_VALUE;

  public LinkListPropertyInformation(Method method) {
    super(method);
  }

  @Override
  public final CapPropertyDescriptorType getType() {
    return CapPropertyDescriptorType.LINK;
  }

  public ContentBeanInformation getLinkType() {
    return linkType;
  }

  public void setLinkType(ContentBeanInformation linkType) {
    this.linkType = linkType;
  }

  public int getMin() {
    return min;
  }

  public void setMin(int min) {
    this.min = min;
  }

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
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

    LinkListPropertyInformation that = (LinkListPropertyInformation) o;

    if (max != that.max) {
      return false;
    }
    if (min != that.min) {
      return false;
    }
    if (linkType != null ? !linkType.equals(that.linkType) : that.linkType != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (linkType != null ? linkType.hashCode() : 0);
    result = 31 * result + min;
    result = 31 * result + max;
    return result;
  }

  @Override
  public String toString() {
    return "LinkListPropertyInformation{" +
        "method=" + getMethod() +
        ", documentTypePropertyName='" + getDocumentTypePropertyName() + '\'' +
        "max=" + max +
        ", min=" + min +
        ", linkType=" + linkType +
        '}';
  }
}
