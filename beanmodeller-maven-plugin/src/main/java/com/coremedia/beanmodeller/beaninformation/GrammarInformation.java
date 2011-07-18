package com.coremedia.beanmodeller.beaninformation;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 07.03.11
 * Time: 12:55
 */
public class GrammarInformation {

  private String grammarName = MarkupPropertyInformation.COREMEDIA_RICHTEXT_GRAMMAR_NAME;
  private URL grammarURL = null;
  private List<String> grammarLocations = new LinkedList<String>();

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
   * the location where the grammars can be loaded by the content server
   *
   * @return the location of the grammars if given
   */
  public List<String> getGrammarLocations() {
    return grammarLocations;
  }

  /**
   * add a location where the grammars can be loaded by the content server
   *
   * @param grammarLocation the location of the grammar (can be null)
   */
  public void addGrammarLocation(String grammarLocation) {
    this.grammarLocations.add(grammarLocation);
  }


  @Override
  public String toString() {
    return "GrammarInformation{" +
        "grammarName='" + grammarName + '\'' +
        ", grammarURL=" + grammarURL +
        ", grammarLocation='" + grammarLocations + '\'' +
        '}';
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

    if (grammarLocations != null ? !grammarLocations.equals(that.grammarLocations) : that.grammarLocations != null) {
      return false;
    }
    if (grammarName != null ? !grammarName.equals(that.grammarName) : that.grammarName != null) {
      return false;
    }
    if (grammarURL != null ? !grammarURL.equals(that.grammarURL) : that.grammarURL != null) { //NOSONAR - this is slow but we are runnning it single threaded anyway
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = grammarName != null ? grammarName.hashCode() : 0;
    result = 31 * result + (grammarURL != null ? grammarURL.hashCode() : 0); //NOSONAR - this is slow but we are runnning it single threaded anyway
    result = 31 * result + (grammarLocations != null ? grammarLocations.hashCode() : 0);
    return result;
  }
}
