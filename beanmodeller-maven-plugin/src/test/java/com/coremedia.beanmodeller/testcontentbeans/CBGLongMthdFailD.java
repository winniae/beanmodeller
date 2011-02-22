package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * Date: Jan 27, 2011
 * Time: 9:46:59 AM
 * <p/>
 * <p/>
 * Class with an overly long method name. This is to test if the Beangenerator can map the method name
 * to a content-property name. Property names are limited due to database constraints.
 * <p/>
 * This fails even with annotation, because one property name is defined twice.
 */
@ContentBean
public abstract class CBGLongMthdFailD extends CBGContent {
  /**
   * Abstract Method. Will be implemented by Beangenerator.
   * <p/>
   * The method name has more than fifty characters. Goal is to test, how the Beangerenerator handles
   * this. Long names are known to not work in CoreMedia CMS.
   * <p/>
   * The annotation changes the methods name, but it appears twice.
   *
   * @return random number, not interesting
   */
  @ContentProperty(propertyName = "LongJane")
  public abstract Integer getMethodWithAnOverlyLongMethodNameOfFiftyCharactersAndWrongAnnotated();

  /**
   * Abstract Method. Will be implemented by Beangenerator.
   * <p/>
   * The method name has more than fifty characters. Goal is to test, how the Beangerenerator handles
   * this. Long names are known to not work in CoreMedia CMS.
   * <p/>
   * The annotation changes the methods name, but it appears twice.
   *
   * @return random number, not interesting
   */
  @ContentProperty(propertyName = "LongJane")
  public abstract Integer getMethodWithAnOverlyLongMethodNameOfFiftyCharactersAndWrongAnnotated2();

}
