package com.coremedia.beanmodeller.processors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Doctype annotation processor. Used to process anotations at compile time
 */
@SupportedAnnotationTypes("ContentBean")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class DoctypeAnnotationProcessor extends AbstractProcessor {
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    for (TypeElement element : annotations) {
      System.out.println("Name: " + element.getQualifiedName());
    }

    return true;
  }
}
