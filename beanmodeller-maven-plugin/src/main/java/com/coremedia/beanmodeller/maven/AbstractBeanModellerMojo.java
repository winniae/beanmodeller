package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.beaninformation.ContentBeanHierarchy;
import com.coremedia.beanmodeller.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.Set;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 07.02.11
 * Time: 14:47
 */
public abstract class AbstractBeanModellerMojo extends AbstractMojo {
  /**
   * Path for searching abstract content beans.
   *
   * @parameter
   */
  private String contentBeanPackage;

  /**
   * Path for searching abstract content beans.
   *
   * @parameter default-value="${project}
   */
  private MavenProject project;

  /**
   * Default char length for String Properties when no explicit value is provided.
   *
   * @parameter default-value=32
   */
  private int defaultPropertyStringLength;

  /**
   * Max length of property names to validate against.
   *
   * @parameter default-value=18
   */
  private int maxPropertyNameLength = ContentBeanAnalyzator.MAX_PROPERTY_LENGTH;

  /**
   * Max length of doctype names to validate against.
   *
   * @parameter default-value=15
   */
  private int maxDoctypeNameLength = ContentBeanAnalyzator.MAX_CONTENT_TYPE_LENGTH;

  private long startTime;
  private Long lastMeasurement = null;

  protected Set<ContentBeanInformation> analyzeContentBeans() throws MojoFailureException, MojoExecutionException {
    //create the analyzator
    ContentBeanAnalyzator analyzer = new ContentBeanAnalyzator();
    //set the logging
    analyzer.setLog(getLog());
    // set parameters
    analyzer.setPropertyDefaultStringLength(defaultPropertyStringLength);

    analyzer.setMaxDoctypeNameLength(maxDoctypeNameLength);
    analyzer.setMaxPropertyNameLength(maxPropertyNameLength);

    // start analyzation
    analyzer.findContentBeans(contentBeanPackage);
    ContentBeanHierarchy hierarchy = null;
    try {
      hierarchy = analyzer.analyzeContentBeanInformation();
    }
    catch (ContentBeanAnalyzationException e) {
      throw new MojoFailureException("Unable to analyze the content beans:" + formatErrors(e), e);
    }
    Set<ContentBeanInformation> roots;
    roots = hierarchy.getRootBeanInformation();
    return roots;
  }

  private String formatErrors(ContentBeanAnalyzationException e) {
    List<ContentBeanAnalyzationException.ContentBeanAnalyzationError> errors = e.getErrors();
    StringBuffer message = new StringBuffer();
    for (ContentBeanAnalyzationException.ContentBeanAnalyzationError error : errors) {
      message.append("\n\t");
      message.append(error.toString());
    }
    message.append("\n");
    return message.toString();
  }

  public long getTimeSinceLastMeasurement() {
    if (lastMeasurement == null) {
      lastMeasurement = System.currentTimeMillis();
      return lastMeasurement - startTime;
    }
    else {
      long now = System.currentTimeMillis();
      long result = now - lastMeasurement;
      lastMeasurement = now;
      return result;
    }
  }

  protected void startTimeMeasurements() {
    startTime = System.currentTimeMillis();
  }

  public long getTimeSinceStart() {
    return System.currentTimeMillis() - startTime;
  }

  public String getContentBeanPackage() {
    return contentBeanPackage;
  }

  public void setContentBeanPackage(String contentBeanPackage) {
    this.contentBeanPackage = contentBeanPackage;
  }

  public MavenProject getProject() {
    return project;
  }

  public void setProject(MavenProject project) {
    this.project = project;
  }

}
