package com.coremedia.beanmodeller.beaninformation;

import com.coremedia.cap.common.CapPropertyDescriptorType;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 * <p/>
 * No special handling.
 */
public class MarkupPropertyInformation extends AbstractPropertyInformation {

  public static final String COREMEDIA_RICHTEXT_GRAMMAR_NAME = "coremedia-richtext-1.0";

  private List<GrammarInformation> grammarInformations = new LinkedList<GrammarInformation>();

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
   * Which grammars are used
   * if set to null the default richtext grammar is used
   *
   * @return the grammar of this markup
   */
  public List<GrammarInformation> getGrammarInformation() {
    return grammarInformations;
  }

  /**
   * set the grammar information
   *
   * @param grammarInformation the grammar information
   */
  public void addGrammarInformation(GrammarInformation grammarInformation) {
    this.grammarInformations.add(grammarInformation);
  }

  @Override
  public String getHumanUnderstandableRepresentation() {
    StringBuilder builder = new StringBuilder();
    if (grammarInformations.isEmpty()) {
      builder.append("Richtext property ");
    }
    else {
      builder.append("XML property ");
    }
    builder.append(getDocumentTypePropertyName());
    builder.append(" for ");
    builder.append(getMethod().getName());
    for (GrammarInformation grammarInformation : grammarInformations) {
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
        "grammarInformation=" + grammarInformations +
        '}';
  }
}
