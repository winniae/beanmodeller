package com.coremedia.beanmodeller.processors.analyzator;

/**
 * User: marcus
 * Date: 20.02.13
 * Time: 19:09
 */
public enum MethodReturnTypeResult {
    OK, //the return type is OK
    PRIMITIVE, //the return type is a primitive and not ok
    NO_CONTENT_BEAN //the return type is no content bean
}
