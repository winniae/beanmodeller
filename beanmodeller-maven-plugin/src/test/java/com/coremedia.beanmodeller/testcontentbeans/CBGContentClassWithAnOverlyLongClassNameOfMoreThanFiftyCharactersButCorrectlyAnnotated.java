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
@ContentBean(doctypeName = "LongJohn")
public abstract class CBGContentClassWithAnOverlyLongClassNameOfMoreThanFiftyCharactersButCorrectlyAnnotated extends CBGContent {

  /**
   * Abstract Method. Will be implemented by Beangenerator.
   *
   * @return random number, not interesting
   */
  public abstract Integer getSomeInt();

}
