package com.coremedia.beanmodeller.beaninformation;

import java.util.Set;

/**
 * All necessary in formation for a single content bean
 */
public interface ContentBeanInformation {

  /**
   * The parent content bean of this content bean – or null if there is no parent (then Document is parent).
   *
   * @return the parent content Bean
   */
  ContentBeanInformation getParent();

  /**
   * The content beans which are direct descendants of this content bean.
   *
   * @return the direct descendant content beans. Returns an empty set if there is none.
   */
  Set<? extends ContentBeanInformation> getChilds();

  /**
   * The name of the document type of this content bean.
   *
   * @return the name of the document. Never <code>null</code>
   */
  String getDocumentName();

  /**
   * The (long) name of this bean. It will most probably be the class name. But it does not have any technical
   * restrictions in length.
   *
   * @return The complete name of this content bean.
   */
  String getName();

  /**
   * The real class that is this content bean.
   *
   * @return
   */
  Class getContentBean();


  /**
   * Properties of this content bean TODO inheritted properties, too???
   *
   * @return Set containing all Properties of this contentbean or empty Set if there are non.
   */
  Set<? extends PropertyInformation> getProperties();

  /**
   * Is this an abstract document type?
   *
   * @return if the doc type is abstract
   */
  public boolean isAbstract();
}
