package com.coremedia.beanmodeller.processors;

import com.coremedia.cap.common.CapPropertyDescriptorType;

import java.lang.reflect.Method;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * <p/>
 * No special handling.
 */
public class MarkupPropertyInformation extends AbstractPropertyInformation {

  String grammar = "coremedia-richtext-1.0";

  public MarkupPropertyInformation(Method method) {
    super(method);
  }

  @Override
  public final CapPropertyDescriptorType getType() {
    return CapPropertyDescriptorType.MARKUP;
  }

  public void setGrammar(String grammar) {
    this.grammar = grammar;
  }

  public String getGrammar() {
    return grammar;
  }
}
