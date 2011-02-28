package com.coremedia.beanmodeller.processors.beaninformation;

import com.coremedia.cap.common.CapPropertyDescriptorType;

import java.lang.reflect.Method;
import java.net.URL;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * <p/>
 * No special handling.
 */
public class MarkupPropertyInformation extends AbstractPropertyInformation {

  public static final String COREMEDIA_RICHTEXT_GRAMMAR_NAME = "coremedia-richtext-1.0";

  private String grammarName = COREMEDIA_RICHTEXT_GRAMMAR_NAME;
  private URL grammarURL = null;
  private String grammarRoot;

  public MarkupPropertyInformation(Method method) {
    super(method);
  }

  @Override
  public final CapPropertyDescriptorType getType() {
    return CapPropertyDescriptorType.MARKUP;
  }

  public void setGrammarName(String grammarName) {
    this.grammarName = grammarName;
  }

  public String getGrammarName() {
    return grammarName;
  }

  public URL getGrammarURL() {
    return grammarURL;
  }

  public void setGrammarURL(URL grammarURL) {
    this.grammarURL = grammarURL;
  }

  public String getGrammarRoot() {
    return grammarRoot;
  }

  public void setGrammarRoot(String grammarRoot) {
    this.grammarRoot = grammarRoot;
  }
}
