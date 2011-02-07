package com.coremedia.beanmodeller.maven;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.processors.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.ContentBeanAnalyzerException;
import com.coremedia.beanmodeller.processors.analyzator.ContentBeanAnalyzator;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshaler;
import com.coremedia.beanmodeller.processors.doctypegenerator.DocTypeMarshalerException;
import com.coremedia.schemabeans.DocType;
import com.coremedia.schemabeans.DocumentTypeModel;
import com.coremedia.schemabeans.ObjectFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
   * @parameter expression="${beans.src.path}" default-value="com.coremedia.schemabeans"
   */
  private Object beanSrcPath;

  /**
   * Path for searching abstract content beans.
   * @parameter expression="${abstract.bean.path}" default-value="."
   */
  private String abstractBeanPath;

  /**
   * Default length for string properties
   * @parameter default-value="32"
   */
  private Integer propertyDefaultStringLength;

  /**
   * Minimal number of items in LinkList
   * @parameter default-value="0"
   */
  private Integer propertyDefaultLinkListMin;

  /**
   * Maximum number of items in LinkList
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


/*    // Find annotated beans
ClassPathScanningCandidateComponentProvider
scanner = new ClassPathScanningCandidateComponentProvider(false);
scanner.addIncludeFilter(new AnnotationTypeFilter(ContentBean.class));
for (BeanDefinition bd : scanner.findCandidateComponents("com.coremedia.testcontentbeans")) {
    System.out.println("Found annotated class marked as contentbean: " + bd.getBeanClassName());
}

// Create Doctype xml
ObjectFactory of = new ObjectFactory();
DocumentTypeModel documentTypeModel = of.createDocumentTypeModel();

DocType docType = of.createDocType();
docType.setName("CMObject");

documentTypeModel.getXmlGrammarOrXmlSchemaOrDocType().add(docType);

try {
    JAXBContext jc = JAXBContext.newInstance("com.coremedia.schemabeans");
    Marshaller m = jc.createMarshaller();
    m.marshal( documentTypeModel, System.out );
} catch (JAXBException e) {
    e.printStackTrace();
}*/

  }


}
