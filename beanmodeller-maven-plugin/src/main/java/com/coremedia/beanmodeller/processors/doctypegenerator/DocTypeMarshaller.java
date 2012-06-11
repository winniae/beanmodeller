package com.coremedia.beanmodeller.processors.doctypegenerator;

import com.coremedia.beanmodeller.beaninformation.BlobPropertyInformation;
import com.coremedia.beanmodeller.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.beaninformation.GrammarInformation;
import com.coremedia.beanmodeller.beaninformation.LinkListPropertyInformation;
import com.coremedia.beanmodeller.beaninformation.MarkupPropertyInformation;
import com.coremedia.beanmodeller.beaninformation.PropertyInformation;
import com.coremedia.beanmodeller.beaninformation.StringPropertyInformation;
import com.coremedia.beanmodeller.processors.MavenProcessor;
import com.coremedia.schemabeans.BlobProperty;
import com.coremedia.schemabeans.DocType;
import com.coremedia.schemabeans.DocumentTypeModel;
import com.coremedia.schemabeans.Import;
import com.coremedia.schemabeans.IndexablePropertyDescriptor;
import com.coremedia.schemabeans.LinkListProperty;
import com.coremedia.schemabeans.ObjectFactory;
import com.coremedia.schemabeans.StringProperty;
import com.coremedia.schemabeans.XmlProperty;
import com.coremedia.schemabeans.XmlSchema;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
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
public class DocTypeMarshaller extends MavenProcessor {

  public static final String XML_SCHEMA_NAME = "http://www.w3.org/2001/XMLSchema";
  private Set<ContentBeanInformation> rootBeanInformations = null;
  private ObjectFactory objectFactory = null;
  private OutputStream outputStream = null;
  private Map<String, GrammarInformation> foundMarkupSchemaDefinitions = new HashMap<String, GrammarInformation>();
  private Map<String, Object> schemaReferences = new HashMap<String, Object>();

  /**
   * global store to remember known DocTypes. This is required when linking properties back to DocTypes.
   */
  private Map<String, DocType> knownDoctypes = new HashMap<String, DocType>();

  /**
   * @param rootBeanInformations Set of RootBeanInformation objects whose hierarchies must be marshaled
   */
  public DocTypeMarshaller(Set<ContentBeanInformation> rootBeanInformations, OutputStream outputStream) {
    this.rootBeanInformations = rootBeanInformations;
    this.objectFactory = new ObjectFactory();
    this.outputStream = outputStream;
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
   * @throws DocTypeMarshallerException if it cannot write the XML
   */
  public void marshallDoctype() throws DocTypeMarshallerException {
    // there must be
    if (this.rootBeanInformations == null) {
      throw new DocTypeMarshallerException(DocTypeMarshallerException.ERROR_MARSHALING);
    }

    getLog().info("Creating doctype XML");

    DocumentTypeModel documentTypeModel = objectFactory.createDocumentTypeModel();
    //TODO this should be a parameter or something
    documentTypeModel.setTitle("telekom-document-type");

    SortedSet<ContentBeanInformation> sortedRootBeansInformation = getSortedRootBeanInformation();

    getGrammars(sortedRootBeansInformation);

    addGrammars(documentTypeModel);

    getAndAddImports(documentTypeModel, sortedRootBeansInformation);

    getChildDocTypes(documentTypeModel, sortedRootBeansInformation);

    getProperties(sortedRootBeansInformation);

    writeDocTypeModel(documentTypeModel);
  }

  private void addGrammars(DocumentTypeModel documentTypeModel) {
    List<Object> elements = documentTypeModel.getXmlGrammarOrXmlSchemaOrImportDocType();
    SortedSet<String> schemaNames = new TreeSet<String>();
    schemaNames.addAll(foundMarkupSchemaDefinitions.keySet());
    List<XmlSchema> schemas = new LinkedList<XmlSchema>();
    for (String grammarName : schemaNames) {
      GrammarInformation grammarInformation = foundMarkupSchemaDefinitions.get(grammarName);
      XmlSchema schema = objectFactory.createXmlSchema();
      schema.setName(grammarName);
      // create white space separated list of schema locations
      StringBuilder schemaLocations = new StringBuilder();
      for (String s : grammarInformation.getGrammarLocations()) {
        schemaLocations.append(s).append(" ");
      }
      schema.setSchemaLocation(schemaLocations.toString().trim());
      schema.setLanguage(XML_SCHEMA_NAME);
      //TODO we should als support public IDs via real internet URLS
      schemas.add(schema);
      schemaReferences.put(grammarName, schema);
    }
    elements.addAll(0, schemas);
    //adding the coremedia richtext grammar
    Import importElement = objectFactory.createImport();
    importElement.setName(MarkupPropertyInformation.COREMEDIA_RICHTEXT_GRAMMAR_NAME);
    elements.add(0, objectFactory.createImportGrammar(importElement));
    schemaReferences.put(MarkupPropertyInformation.COREMEDIA_RICHTEXT_GRAMMAR_NAME, importElement);
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
          GrammarInformation grammarInformation = markupPropertyInformation.getGrammarInformation();
          if (grammarInformation != null) {
            String grammarName = grammarInformation.getGrammarName();
            foundMarkupSchemaDefinitions.put(grammarName, grammarInformation);
          }
        }
      }
      //and then down the hierarchy
      findGrammars(beanInformation.getChilds());
    }
  }

  private void getAndAddImports(DocumentTypeModel documentTypeModel, SortedSet<ContentBeanInformation> beanInformations) {
    for (ContentBeanInformation beanInformation : beanInformations) {
      final String externalParentDocumentName = beanInformation.getExternalParentDocumentName();
      if (StringUtils.isNotBlank(externalParentDocumentName)) {
        // create XML for ImportDocType name="parentDocType"
        final Import anImport = objectFactory.createImport();
        anImport.setName(externalParentDocumentName);
        final JAXBElement<Import> importDocType = objectFactory.createImportDocType(anImport);
        documentTypeModel.getXmlGrammarOrXmlSchemaOrImportDocType().add(importDocType);
      }
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
      documentTypeModel.getXmlGrammarOrXmlSchemaOrImportDocType().addAll(extractChildDocTypes(contentBeanInformation, null));
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

  private void writeDocTypeModel(DocumentTypeModel documentTypeModel) throws DocTypeMarshallerException {
    // setup default output stream
    if (this.outputStream == null) {
      throw new DocTypeMarshallerException("Writing doctype XML");
    }

    // Write xml to output stream
    try {
      JAXBContext jc = JAXBContext.newInstance("com.coremedia.schemabeans");
      Marshaller m = jc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      m.marshal(documentTypeModel, this.outputStream);
    }
    catch (JAXBException e) {
      throw new DocTypeMarshallerException("unable to write the doctypes xml", e);
    }
    finally {
      try {
        this.outputStream.flush();
        this.outputStream.close();
      }
      catch (IOException e) {
        throw new DocTypeMarshallerException("unable to write the doctypes xml", e);
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
    getLog().info("Writing doctype for " + contentBeanInformation);
    // ContentBeanInformation contains information for a one DocType
    DocType currentDocType = objectFactory.createDocType();
    currentDocType.setName(contentBeanInformation.getDocumentName());

    //set abstract doctypes to abstract
    //not done for non abstract since it is the default
    if (contentBeanInformation.isAbstract()) {
      currentDocType.setAbstract(true);
    }

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
    if (parentDocType != null) {
      currentDocType.setParent(parentDocType);
    }
    else if (StringUtils.isNotBlank(contentBeanInformation.getExternalParentDocumentName())) {
      // parentdoctype is explicitly set
      final Import anImport = objectFactory.createImport();
      anImport.setName(contentBeanInformation.getExternalParentDocumentName());
      currentDocType.setParent(anImport);
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
      Object element = createPropertyDescriptionFromPropertyInformation(propertyInformation);

      if (element != null) {
        currentDocType.getBlobPropertyOrDatePropertyOrIntProperty().add(element);
      }
    }

    // recursive call for each child
    for (ContentBeanInformation contentBeanInformationChild : contentBeanInformation.getChilds()) {
      extractProperties(contentBeanInformationChild);
    }
  }

  private Object createPropertyDescriptionFromPropertyInformation(PropertyInformation propertyInformation) {
    IndexablePropertyDescriptor descriptor = objectFactory.createIndexablePropertyDescriptor();
    switch (propertyInformation.getType()) {
      case STRING:
        final StringProperty stringProperty = objectFactory.createStringProperty();
        stringProperty.setName(propertyInformation.getDocumentTypePropertyName());
        stringProperty.setLength(BigInteger.valueOf(((StringPropertyInformation) propertyInformation).getLength()));
        return stringProperty;

      case INTEGER:
        JAXBElement<IndexablePropertyDescriptor> intProperty = objectFactory.createIntProperty(descriptor);
        intProperty.getValue().setName(propertyInformation.getDocumentTypePropertyName());
        return intProperty;

      case LINK:
        return createLinkProperty((LinkListPropertyInformation) propertyInformation);

      case DATE:
        JAXBElement<IndexablePropertyDescriptor> dateProperty = objectFactory.createDateProperty(descriptor);
        dateProperty.getValue().setName(propertyInformation.getDocumentTypePropertyName());
        return dateProperty;

      case MARKUP:
        return createMarkupProperty((MarkupPropertyInformation) propertyInformation);

      case BLOB:
        return createBlobProperty((BlobPropertyInformation) propertyInformation);

      default:
        return null;
    }
  }

  private Object createBlobProperty(BlobPropertyInformation propertyInformation) {
    final BlobProperty blobProperty = objectFactory.createBlobProperty();
    blobProperty.setName(propertyInformation.getDocumentTypePropertyName());
    blobProperty.setMimeType(propertyInformation.getAllowedMimeTypes());
    //this is possible too
    //blobProperty.setExtension();
    //blobProperty.setOverride();
    return blobProperty;
  }

  private Object createMarkupProperty(MarkupPropertyInformation propertyInformation) {
    final XmlProperty xmlProperty = objectFactory.createXmlProperty();
    xmlProperty.setName(propertyInformation.getDocumentTypePropertyName());
    GrammarInformation grammarInformation = propertyInformation.getGrammarInformation();
    if (grammarInformation != null) {
      // get only the first grammar
      xmlProperty.setGrammar(grammarInformation.getGrammarName());
    }
    else {
      xmlProperty.setGrammar(MarkupPropertyInformation.COREMEDIA_RICHTEXT_GRAMMAR_NAME);
    }
    //rewrite the grammar object to it's reference
    xmlProperty.setGrammar(schemaReferences.get(xmlProperty.getGrammar()));
    return xmlProperty;
  }

  private Object createLinkProperty(LinkListPropertyInformation propertyInformation) {
    final LinkListProperty listProperty = objectFactory.createLinkListProperty();
    final String docTypeName = propertyInformation.getLinkType().getDocumentName();
    listProperty.setName(propertyInformation.getDocumentTypePropertyName());
    listProperty.setLinkType(knownDoctypes.get(docTypeName));
    listProperty.setMin(BigInteger.valueOf(propertyInformation.getMin()));
    listProperty.setMax(BigInteger.valueOf(propertyInformation.getMax()));
    return listProperty;
  }

  public Map<String, GrammarInformation> getFoundMarkupSchemaDefinitions() {
    return foundMarkupSchemaDefinitions;
  }
}
