package com.coremedia.beanmodeller.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Telekom .COM Relaunch 2011
 * <p> Triggers generation of DocType XML</p>
 *
 * @goal generate-doctypes
 */
public class GenerateDoctypesMojo extends AbstractBeanModellerMojo {

  /**
   * Path for generating and reading JAXB property beans.
   *
   * @parameter expression="${beans.src.path}" default-value="com.coremedia.schemabeans"
   */
  private Object beanSrcPath;

  /**
   * Path for searching abstract content beans.
   *
   * @parameter expression="${abstract.bean.path}" default-value="."
   */
  private String abstractBeanPath;

  /**
   * Default length for string properties
   *
   * @parameter default-value="32"
   */
  private Integer propertyDefaultStringLength;

  /**
   * Minimal number of items in LinkList
   *
   * @parameter default-value="0"
   */
  private Integer propertyDefaultLinkListMin;

  /**
   * Maximum number of items in LinkList
   *
   * @parameter default-value="1000000"
   */
  private Integer propertyDefaultLinkListMax;

  public void execute() throws MojoExecutionException, MojoFailureException {


  }


}
