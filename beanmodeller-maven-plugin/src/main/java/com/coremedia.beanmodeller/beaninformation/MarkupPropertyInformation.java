package com.coremedia.beanmodeller.beaninformation;

import com.coremedia.cap.common.CapPropertyDescriptorType;

import java.lang.reflect.Method;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * <p/>
 * No special handling.
 */
public class MarkupPropertyInformation extends AbstractPropertyInformation {

  public static final String COREMEDIA_RICHTEXT_GRAMMAR_NAME = "coremedia-richtext-1.0";

  private GrammarInformation grammarInformation = null;

  /**
   * Creates a markup information object. You should not have any need to do this
   *
   * @param method
   */
  public MarkupPropertyInformation(Method method) {
    super(method);
  }

  @Override
  public final CapPropertyDescriptorType getType() {
    return CapPropertyDescriptorType.MARKUP;
  }

  /**
   * Which grammar is used
   * if set to null the default richtext grammar is used
   *
   * @return the grammar of this markup
   */
  public GrammarInformation getGrammarInformation() {
    return grammarInformation;
  }

  /**
   * set the grammar information
   *
   * @param grammarInformation the grammar information
   */
  public void setGrammarInformation(GrammarInformation grammarInformation) {
    this.grammarInformation = grammarInformation;
  }

  @Override
  public String getHumanUnderstandableRepresentation() {
    StringBuilder builder = new StringBuilder();
    if (grammarInformation == null) {
      builder.append("Richtext property ");
    }
    else {
      builder.append("XML property ");
    }
    builder.append(getDocumentTypePropertyName());
    builder.append(" for ");
    builder.append(getMethod().getName());
    if (grammarInformation != null) {
      builder.append("(Schema: ");
      builder.append(grammarInformation.getGrammarName());
      builder.append(", ");
      builder.append(grammarInformation.getGrammarLocation());
      builder.append(", @ ");
      builder.append(grammarInformation.getGrammarURL());
      builder.append(')');
    }
    return builder.toString();

  }

  @Override
  public String toString() {
    return "MarkupPropertyInformation{" +
        "method=" + getMethod() +
        ", documentTypePropertyName='" + getDocumentTypePropertyName() + '\'' +
        "grammarInformation=" + grammarInformation +
        '}';
  }
}
