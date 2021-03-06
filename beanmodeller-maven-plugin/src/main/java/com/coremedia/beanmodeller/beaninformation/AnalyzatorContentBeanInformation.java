package com.coremedia.beanmodeller.beaninformation;

import java.util.HashSet;
import java.util.Set;

/**
 * The ContentBeanInformation generated by the Analyzator.
 */
public class AnalyzatorContentBeanInformation implements ContentBeanInformation {
  private String name;
  private Class contentBean;
  private String documentName;
  private boolean isAbstract = false;
  private String aspectDocumentName;

  private ContentBeanInformation parent;
  private Set<ContentBeanInformation> childs = new HashSet<ContentBeanInformation>();
  private Set<PropertyInformation> properties = new HashSet<PropertyInformation>();
  private Set<CacheKeyableMethodInformation> cacheKeyables = new HashSet<CacheKeyableMethodInformation>();

  public AnalyzatorContentBeanInformation(Class beanClass) {
    this.contentBean = beanClass;
    name = contentBean.getSimpleName();
  }

  @Override
  public ContentBeanInformation getParent() {
    return parent;
  }

  @Override
  public Set<? extends ContentBeanInformation> getChilds() {
    return childs;
  }

  @Override
  public String getDocumentName() {
    return documentName;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Class getContentBean() {
    return contentBean;
  }

  @Override
  public Set<? extends PropertyInformation> getProperties() {
    return properties;
  }

  @Override
  public Set<CacheKeyableMethodInformation> getCacheKeyables() {
    return cacheKeyables;
  }

  public void setParent(AnalyzatorContentBeanInformation parent) {
    this.parent = parent;
    parent.addChild(this);
  }

  public void addChild(AnalyzatorContentBeanInformation child) {
    childs.add(child);
    child.parent = this;
  }

  public void setDocumentName(String documentName) {
    this.documentName = documentName;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setContentBean(Class contentBean) {
    this.contentBean = contentBean;
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public void setAbstract(boolean anAbstract) {
    isAbstract = anAbstract;
  }

  public String getAspectDocumentName() {
    return aspectDocumentName;
  }

  public void setAspectDocumentName(String aspectDocumentName) {
    this.aspectDocumentName = aspectDocumentName;
  }

  public void addProperty(PropertyInformation propertyInformation) {
    properties.add(propertyInformation);
  }

  public void addCacheKeyable(CacheKeyableMethodInformation cacheKeyableMethodInformation) {
    cacheKeyables.add(cacheKeyableMethodInformation);
  }

  @Override
  public String getHumanUnderstandableRepresentation() {
    StringBuilder builder = new StringBuilder();
    if (isAbstract) {
      builder.append("Abstract ");
    }
    builder.append(name);
    builder.append(" for ");
    builder.append(contentBean.getCanonicalName());
    builder.append(", document type ");
    builder.append(documentName);
    return builder.toString();
  }

  @Override
  public String toString() {
    return "AnalyzatorContentBeanInformation{" +
        "name='" + name + '\'' +
        ", contentBean=" + contentBean +
        ", documentName='" + documentName + '\'' +
        ", isAbstract=" + isAbstract +
        '}';
  }
}
