package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.processors.ContentBeanAnalyzerException;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshaler;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshalerException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Telekom .COM Relaunch 2011
 * <p> Triggers generation of DocType XML</p>
 *
 * @goal generate-doctypes
 */
public class GenerateDoctypesMojo extends AbstractMojo {

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

    ContentBeanAnalyzator analyzer = new ContentBeanAnalyzator();
    DocTypeMarshaler marshaler = null;

    try {
      analyzer.analyzeContentBeanInformation();
      marshaler = new DocTypeMarshaler(analyzer.getContentBeanRoots());
    }
    catch (ContentBeanAnalyzerException e) {
      getLog().error("Error while running generate-doctypes", e);
    }

    try {
      marshaler.setOutputStream(new FileOutputStream("doctypes.xml"));
      marshaler.marshallDoctype();
    }
    catch (FileNotFoundException e) {
      getLog().error("File could not be created ", e);
    }
    catch (DocTypeMarshalerException e) {
      getLog().error("Error marshaling doctype ", e);
    }

  }


}
