package com.coremedia.beanmodeller.beaninformation;

import java.net.URL;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 07.03.11
 * Time: 12:55
 */
public class GrammarInformation {

  private String grammarName = MarkupPropertyInformation.COREMEDIA_RICHTEXT_GRAMMAR_NAME;
  private URL grammarURL = null;
  private String grammarLocation;

  /**
   * Sets the public name of the grammar
   *
   * @param grammarName the name of the grammar
   */
  public void setGrammarName(String grammarName) {
    this.grammarName = grammarName;
  }

  /**
   * returns the name of the grammar
   *
   * @return the name of the grammar
   */
  public String getGrammarName() {
    return grammarName;
  }

  /**
   * The URL to the real grammar resource (xsd file)
   *
   * @return the url to the grammar resource
   */
  public URL getGrammarURL() {
    return grammarURL;
  }

  /**
   * Sets the URL of the grammar resource (xsd file)
   *
   * @param grammarURL
   */
  public void setGrammarURL(URL grammarURL) {
    this.grammarURL = grammarURL;
  }

  /**
   * the location where the grammar can be loaded by the content server
   *
   * @return the location of the grammar if given
   */
  public String getGrammarLocation() {
    return grammarLocation;
  }

  /**
   * the location where the grammar can be loaded by the content server
   *
   * @param grammarLocation the location of the grammar (can be null)
   */
  public void setGrammarLocation(String grammarLocation) {
    this.grammarLocation = grammarLocation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    GrammarInformation that = (GrammarInformation) o;

    if (grammarLocation != null ? !grammarLocation.equals(that.grammarLocation) : that.grammarLocation != null) {
      return false;
    }
    if (!grammarName.equals(that.grammarName)) {
      return false;
    }
    if (grammarURL != null ? !grammarURL.equals(that.grammarURL) : that.grammarURL != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = grammarName.hashCode();
    result = 31 * result + (grammarURL != null ? grammarURL.hashCode() : 0);
    result = 31 * result + (grammarLocation != null ? grammarLocation.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "GrammarInformation{" +
        "grammarName='" + grammarName + '\'' +
        ", grammarURL=" + grammarURL +
        ", grammarLocation='" + grammarLocation + '\'' +
        '}';
  }
}
