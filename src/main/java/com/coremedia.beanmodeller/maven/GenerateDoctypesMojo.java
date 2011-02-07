package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.schemabeans.DocType;
import com.coremedia.schemabeans.DocumentTypeModel;
import com.coremedia.schemabeans.ObjectFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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
    // Create Doctype xml
    ObjectFactory of = new ObjectFactory();
    DocumentTypeModel documentTypeModel = of.createDocumentTypeModel();

    DocType docType = of.createDocType();
    docType.setName("CMObject");

    documentTypeModel.getXmlGrammarOrXmlSchemaOrDocType().add(docType);

    try {
      JAXBContext jc = JAXBContext.newInstance("com.coremedia.schemabeans");
      Marshaller m = jc.createMarshaller();
      m.marshal(documentTypeModel, System.out);
    }
    catch (JAXBException e) {
      e.printStackTrace();
    }

  }


}
