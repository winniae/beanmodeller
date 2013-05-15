package com.coremedia.beanmodeller.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generate a CoreMedia CacheKey for the annotated method and delegate the CacheKeys evaluate() method to the original
 * method.
 * Makes an easy way to cache methods in the CoreMedia cache.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface CacheKeyable {

    String CACHECLASS_DEFAULT = "beanmodeller.cacheclass";

    /**
     * The cacheclass to associate with the generated CacheKey
     * @return the name of the cacheclass. default to "beanmodeller.cacheclass"
     */
    String cacheClass() default CACHECLASS_DEFAULT;
}
