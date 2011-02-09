package com.coremedia.beanmodeller.processors.doctypegenerator;

import com.coremedia.beanmodeller.processors.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.LinkListPropertyInformation;
import com.coremedia.beanmodeller.processors.MarkupPropertyInformation;
import com.coremedia.beanmodeller.processors.MavenProcessor;
import com.coremedia.beanmodeller.processors.PropertyInformation;
import com.coremedia.beanmodeller.processors.StringPropertyInformation;
import com.coremedia.schemabeans.DocType;
import com.coremedia.schemabeans.DocumentTypeModel;
import com.coremedia.schemabeans.LinkListProperty;
import com.coremedia.schemabeans.ObjectFactory;
import com.coremedia.schemabeans.Propertydescriptor;
import com.coremedia.schemabeans.StringProperty;
import com.coremedia.schemabeans.XmlGrammar;
import com.coremedia.schemabeans.XmlProperty;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class provides functionality for marshaling document types contained in ContentBeanInformation to an XML document. <br>
 * <p/>
 * Telekom .COM Relaunch 2011
 * User: aratas
 * Date: 31.01.2011
 * Time: 11:30:42
 */
public class DocTypeMarshaler extends MavenProcessor {

  private Set<ContentBeanInformation> rootBeanInformations = null;
  private ObjectFactory objectFactory = null;
  private OutputStream outputStream = null;
  private Map<String, URL> foundMarkupSchemaDefinitions = new HashMap<String, URL>();

  /**
   * global store to remember known DocTypes. This is required when linking properties back to DocTypes.
   */
  private Map<String, DocType> knownDoctypes = new HashMap<String, DocType>();

  /**
   * @param rootBeanInformations Set of RootBeanInformation objects whose hierarchies must be marshaled
   */
  public DocTypeMarshaler(Set<ContentBeanInformation> rootBeanInformations) {
    this.rootBeanInformations = rootBeanInformations;
    this.objectFactory = new ObjectFactory();
  }

  /**
   * Set output stream where to marshall resulting DocType XML.
   * By default XML is printed to File output stream
   *
   * @param outputStream
   */
  public void setOutputStream(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  /**
   * Triggers Marshaller to write XML to output stream
   *
   * @throws throws a DocTypeMarhalerException if it cannot write the XML
   */
  public void marshallDoctype() throws DocTypeMarshalerException {
    // there must be
    if (this.rootBeanInformations == null) {
      throw new DocTypeMarshalerException(DocTypeMarshalerException.ERROR_MARSHALING);
    }

    DocumentTypeModel documentTypeModel = objectFactory.createDocumentTypeModel();
    documentTypeModel.setTitle("telekom-document-type");

    SortedSet<ContentBeanInformation> sortedRootBeansInformation = getSortedRootBeanInformation();

    getGrammars(sortedRootBeansInformation);

    getChildDocTypes(documentTypeModel, sortedRootBeansInformation);

    getProperties(sortedRootBeansInformation);

    writeDocTypeModel(documentTypeModel);

    //copyGrammars();
  }

  private void getGrammars(SortedSet<ContentBeanInformation> sortedRootBeansInformation) {
    findGrammars(sortedRootBeansInformation);

  }

  private void findGrammars(Set<? extends ContentBeanInformation> beanInformations) {
    for (ContentBeanInformation beanInformation : beanInformations) {
      //first check all properties
      for (PropertyInformation propertyInformation : beanInformation.getProperties()) {
        if (propertyInformation instanceof MarkupPropertyInformation) {
          MarkupPropertyInformation markupPropertyInformation = (MarkupPropertyInformation) propertyInformation;
          String grammarName = markupPropertyInformation.getGrammarName();
          URL grammarURL = markupPropertyInformation.getGrammarURL();
          if (grammarURL != null) {
            foundMarkupSchemaDefinitions.put(grammarName, grammarURL);
          }
          else {
            if (!MarkupPropertyInformation.COREMEDIA_RICHTEXT_GRAMMAR_NAME.equals(grammarName)) {
              getLog().warn("No xsd given for " + grammarName);
              //todo we can stop here with an exception
            }
          }
        }
      }
      //and then down the hierarchy
      findGrammars(beanInformation.getChilds());
    }
  }

  private void getProperties(SortedSet<ContentBeanInformation> sortedRootBeansInformation) {
    for (ContentBeanInformation contentBeanInformation : sortedRootBeansInformation) {
      extractProperties(contentBeanInformation);
    }
  }

  private void getChildDocTypes(DocumentTypeModel documentTypeModel, SortedSet<ContentBeanInformation> sortedRootBeansInformation) {
    for (ContentBeanInformation contentBeanInformation : sortedRootBeansInformation) {
      // get doctypes that inherit from this root content bean
      documentTypeModel.getXmlGrammarOrXmlSchemaOrDocType().addAll(extractChildDocTypes(contentBeanInformation, null));
    }
  }

  private SortedSet<ContentBeanInformation> getSortedRootBeanInformation() {
    SortedSet<ContentBeanInformation> sortedRootBeansInformation = new TreeSet<ContentBeanInformation>(
        new Comparator<ContentBeanInformation>() {
          @Override
          public int compare(ContentBeanInformation o1, ContentBeanInformation o2) {
            return o1.getDocumentName().compareTo(o2.getDocumentName());
          }
        });
    sortedRootBeansInformation.addAll(rootBeanInformations);
    return sortedRootBeansInformation;
  }

  private void writeDocTypeModel(DocumentTypeModel documentTypeModel) throws DocTypeMarshalerException {
    // setup default output stream
    if (this.outputStream == null) {
      String xmlFileName = documentTypeModel.getTitle() == null ? documentTypeModel.getTitle() + ".xml" : "document-type-model.xml";
      File outputDir = new File("target");
      File xmlFile = new File(outputDir, xmlFileName);
      try {
        this.outputStream = new FileOutputStream(xmlFile);
      }
      catch (FileNotFoundException e) {
        throw new DocTypeMarshalerException("unable to find output file", e);
      }
    }

    // Write xml to output stream
    try {
      JAXBContext jc = JAXBContext.newInstance("com.coremedia.schemabeans");
      Marshaller m = jc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      m.marshal(documentTypeModel, this.outputStream);
    }
    catch (JAXBException e) {
      throw new DocTypeMarshalerException("unable to write the doctypes xml", e);
    }
    finally {
      try {
        this.outputStream.flush();
        this.outputStream.close();
      }
      catch (IOException e) {
        throw new DocTypeMarshalerException("unable to write the doctypes xml", e);
      }
    }
  }

  /**
   * return a set of DocType objects that contains document type for ContentBeanInformation and all children
   *
   * @param contentBeanInformation
   * @param parentDocType          Document type whose children must be found
   * @return
   */
  private List<DocType> extractChildDocTypes(ContentBeanInformation contentBeanInformation, DocType parentDocType) {
    // ContentBeanInformation contains information for a one DocType
    DocType currentDocType = objectFactory.createDocType();
    currentDocType.setName(contentBeanInformation.getDocumentName());

    knownDoctypes.put(currentDocType.getName(), currentDocType);

    // Will collect current DocType and all child doctypes
    List<DocType> docTypes = new LinkedList<DocType>();
    docTypes.add(currentDocType);


    //  add children recursively to docTypes
    SortedSet<ContentBeanInformation> childBeanInformationsSorted = new TreeSet<ContentBeanInformation>(
        new Comparator<ContentBeanInformation>() {
          @Override
          public int compare(ContentBeanInformation o1, ContentBeanInformation o2) {
            return o1.getDocumentName().compareTo(o2.getDocumentName());
          }
        });

    childBeanInformationsSorted.addAll(contentBeanInformation.getChilds());

    for (ContentBeanInformation cbi : childBeanInformationsSorted) {
      docTypes.addAll(extractChildDocTypes(cbi, currentDocType));
    }

    // Current DocType may have parents
    if (parentDocType != null)

    {
      currentDocType.setParent(parentDocType);
    }

    return docTypes;
  }

  private void extractProperties(ContentBeanInformation contentBeanInformation) {
    DocType currentDocType = knownDoctypes.get(contentBeanInformation.getDocumentName());

    // add Properties
    SortedSet<PropertyInformation> propertyInformationsSorted = new TreeSet<PropertyInformation>(
        new Comparator<PropertyInformation>() {
          @Override
          public int compare(PropertyInformation o1, PropertyInformation o2) {
            return o1.getDocumentTypePropertyName().compareTo(o2.getDocumentTypePropertyName());
          }
        });

    propertyInformationsSorted.addAll(contentBeanInformation.getProperties());

    for (PropertyInformation propertyInformation : propertyInformationsSorted) {
      // XML property descriptor to create
      Propertydescriptor propertydescriptor = createPropertyDescriptionFromPropertyInformation(propertyInformation);

      if (propertydescriptor != null) {
        propertydescriptor.setName(propertyInformation.getDocumentTypePropertyName());
        currentDocType.getBlobPropertyOrDatePropertyOrIntProperty().add(propertydescriptor);
      }
    }

    // recursive call for each child
    for (ContentBeanInformation contentBeanInformationChild : contentBeanInformation.getChilds()) {
      extractProperties(contentBeanInformationChild);
    }
  }

  private Propertydescriptor createPropertyDescriptionFromPropertyInformation(PropertyInformation propertyInformation) {
    switch (propertyInformation.getType()) {
      case STRING:
        final StringProperty stringProperty = objectFactory.createStringProperty();
        stringProperty.setLength(BigInteger.valueOf(((StringPropertyInformation) propertyInformation).getLength()));
        return stringProperty;

      case INTEGER:
        return objectFactory.createIntProperty();

      case LINK:
        final LinkListProperty listProperty = objectFactory.createLinkListProperty();
        final String docTypeName = ((LinkListPropertyInformation) propertyInformation).getLinkType().getDocumentName();
        listProperty.setLinkType(knownDoctypes.get(docTypeName));
        listProperty.setMin(BigInteger.valueOf(((LinkListPropertyInformation) propertyInformation).getMin()));
        listProperty.setMax(BigInteger.valueOf(((LinkListPropertyInformation) propertyInformation).getMax()));
        return listProperty;

      case DATE:
        return objectFactory.createDateProperty();

      case MARKUP:
        final XmlProperty xmlProperty = objectFactory.createXmlProperty();
        final String grammarName = ((MarkupPropertyInformation) propertyInformation).getGrammarName();
        final XmlGrammar grammarProperty = objectFactory.createXmlGrammar();
        grammarProperty.setSystemId(grammarName);
        grammarProperty.setName(grammarName);
        grammarProperty.setRoot(grammarName);
        xmlProperty.setGrammar(grammarProperty);
        return xmlProperty;

      default:
        return null;
    }
  }

}
