package com.coremedia.beanmodeller.testcontentbeans;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.testcontentbeans.testmodel.CBGContent;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * Date: Jan 27, 2011
 * Time: 9:46:59 AM
 * <p/>
 * Class with an overly long class name. This is to test if the Beangenerator can map the class name
 * to a content-documenttype name. Documenttype names are limited due to database constraints.
 */
@ContentBean
public abstract class CBGContentClassWithAnOverlyLongClassNameOfFiftyCharacters extends CBGContent {

  /**
   * Abstract Method. Will be implemented by Beangenerator.
   * <p/>
   * The method name has more than fifty characters. Goal is to test, how the Beangerenerator handles
   * this. Long names are known to not work in CoreMedia CMS.
   *
   * @return random number, not interesting
   */
  public abstract Integer getMethodWithAnOverlyLongMethodNameOfFiftyCharacters();

}
