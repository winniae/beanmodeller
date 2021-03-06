package com.coremedia.beanmodeller.processors.doctypegenerator;

import com.coremedia.beanmodeller.annotations.ContentBean;
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
import com.coremedia.schemabeans.DocTypeAspect;
import com.coremedia.schemabeans.DocumentTypeModel;
import com.coremedia.schemabeans.Import;
import com.coremedia.schemabeans.IndexablePropertyDescriptor;
import com.coremedia.schemabeans.LinkListProperty;
import com.coremedia.schemabeans.ObjectFactory;
import com.coremedia.schemabeans.Propertydescriptor;
import com.coremedia.schemabeans.StringProperty;
import com.coremedia.schemabeans.XmlGrammar;
import com.coremedia.schemabeans.XmlProperty;
import com.coremedia.schemabeans.XmlSchema;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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
  private static final String CLASSPATH = "classpath:";
  private static final String DTD_ELEMENT = "<!ELEMENT";
  private Set<ContentBeanInformation> rootBeanInformations = null;
  private ObjectFactory objectFactory = null;
  private OutputStream outputStream = null;
  private Map<String, GrammarInformation> foundMarkupSchemaDefinitions = new HashMap<String, GrammarInformation>();
  private Map<String, Object> schemaReferences = new HashMap<String, Object>();

  /**
   * global store to remember known DocTypes. This is required when linking properties back to DocTypes.
   */
  private Map<String, Object> knownDoctypes = new HashMap<String, Object>();

  /**
   * title of the doctype.xml, purely cosmetic
   */
  private String doctypeTitle = "beanmodeller-document-type";

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
    documentTypeModel.setTitle(doctypeTitle);

    SortedSet<ContentBeanInformation> sortedRootBeansInformation = getSortedRootBeanInformation();

    getGrammars(sortedRootBeansInformation);

    addGrammars(documentTypeModel);

    addImports(documentTypeModel, sortedRootBeansInformation);

    getChildDocTypes(documentTypeModel, sortedRootBeansInformation);

    getProperties(sortedRootBeansInformation);

    writeDocTypeModel(documentTypeModel);
  }

  private void addGrammars(DocumentTypeModel documentTypeModel) throws DocTypeMarshallerException {
    List<Object> headerElements = documentTypeModel.getXmlGrammarOrXmlSchemaOrImportDocType();

    // sort all found GrammarInformations by name
    SortedSet<String> schemaNames = new TreeSet<String>();
    schemaNames.addAll(foundMarkupSchemaDefinitions.keySet());

    // iterate over all GrammarInformations and create xml objects for each
    List<Object> schemaEntries = new LinkedList<Object>();
    for (String grammarName : schemaNames) {
      Object schema = getXmlObjectForGrammarInformation(foundMarkupSchemaDefinitions.get(grammarName));
      if (schema != null) {
        schemaEntries.add(schema);
        schemaReferences.put(grammarName, schema);
      }
      else {
        // adding as import (e.g. the coremedia richtext grammar)
        Import importElement = objectFactory.createImport();
        importElement.setName(grammarName);
        headerElements.add(0, objectFactory.createImportGrammar(importElement));
        schemaReferences.put(grammarName, importElement);
      }
    }
    // add all schema xml objects
    headerElements.addAll(0, schemaEntries);

    //adding the coremedia richtext grammar, if not there already
    if (!schemaReferences.containsKey(MarkupPropertyInformation.COREMEDIA_RICHTEXT_GRAMMAR_NAME)) {
      Import importElement = objectFactory.createImport();
      importElement.setName(MarkupPropertyInformation.COREMEDIA_RICHTEXT_GRAMMAR_NAME);
      headerElements.add(0, objectFactory.createImportGrammar(importElement));
      schemaReferences.put(MarkupPropertyInformation.COREMEDIA_RICHTEXT_GRAMMAR_NAME, importElement);
    }
  }

  /**
   * Return type is either XMLGrammar or XMLSchema
   *
   * @param grammarInformation
   * @return
   */
  private Object getXmlObjectForGrammarInformation(GrammarInformation grammarInformation) throws DocTypeMarshallerException {
    final String grammarName = grammarInformation.getGrammarName();
    if (grammarName.endsWith(".dtd")) {
      XmlGrammar grammar = objectFactory.createXmlGrammar();
      grammar.setName(grammarName);
      // strip .dtd

      String plainname = grammarName.substring(0, grammarName.length() - 4);

      // just take the first.. can DTDs have a list?
      String grammarLocation = grammarInformation.getGrammarLocations().get(0);
      grammar.setSystemId(grammarLocation);

      grammar.setPublicId("-//" + plainname + "//DTD//EN");

      // read root node from actual content
      String rootElement;
      if (grammarLocation.startsWith(CLASSPATH)) {
        grammarLocation = grammarLocation.substring("classpath:".length());
      }
      // find first ELEMENT from DTD and use that as root
      final InputStream resoruceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(grammarLocation);
      final Scanner scanner = new Scanner(resoruceStream);
      rootElement = scanner.findWithinHorizon(DTD_ELEMENT + " \\w+ ", 0).substring(DTD_ELEMENT.length()).trim();

      grammar.setRoot(rootElement);

      return grammar;
    } else if (grammarName.endsWith(".xsd")) {
      XmlSchema schema = objectFactory.createXmlSchema();
      schema.setName(grammarName);
      // create white space separated list of schema locations
      StringBuilder schemaLocations = new StringBuilder();
      for (String s : grammarInformation.getGrammarLocations()) {
        schemaLocations.append(s).append(" ");
      }
      schema.setSchemaLocation(schemaLocations.toString().trim());
      schema.setLanguage(XML_SCHEMA_NAME);
      return schema;
    } else {
      // adding as import (e.g. the coremedia richtext grammar)
      // mark with null ...
      return null;
    }

//    throw new DocTypeMarshallerException("unrecognized XML schema "+grammarInformation);
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

  private void addImports(DocumentTypeModel documentTypeModel, Set<? extends ContentBeanInformation> beanInformations) {
    for (ContentBeanInformation beanInformation : beanInformations) {
      // create import for every doctype that will get an aspect
      final String aspectDocumentName = beanInformation.getAspectDocumentName();
      if (!aspectDocumentName.equals(ContentBean.DOC_TYPE_ASPECT_DISABLED)) {
        // create XML for ImportDocType name="docType"
        final Import anImport = objectFactory.createImport();
        anImport.setName(aspectDocumentName);
        final JAXBElement<Import> importDocType = objectFactory.createImportDocType(anImport);
        documentTypeModel.getXmlGrammarOrXmlSchemaOrImportDocType().add(importDocType);
      }
      // recursively
      addImports(documentTypeModel, beanInformation.getChilds());
    }
  }

  private void getProperties(SortedSet<ContentBeanInformation> sortedRootBeansInformation) {
    for (ContentBeanInformation contentBeanInformation : sortedRootBeansInformation) {
      extractProperties(contentBeanInformation, Collections.<PropertyInformation>emptySet());
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
    } catch (JAXBException e) {
      throw new DocTypeMarshallerException("unable to write the doctypes xml", e);
    } finally {
      try {
        this.outputStream.flush();
        this.outputStream.close();
      } catch (IOException e) {
        throw new DocTypeMarshallerException("unable to write the doctypes xml", e);
      }
    }
  }

  /**
   * return a set of DocType objects that contains document type for ContentBeanInformation and all children
   *
   * @param contentBeanInformation
   * @param parentDocType          Document type whose children must be found
   * @return list of doctypes or doctypeaspects
   */
  private List<Object> extractChildDocTypes(ContentBeanInformation contentBeanInformation, Object parentDocType) {
    getLog().info("Writing doctype for " + contentBeanInformation);

    // DocType or DocTypeAspect!
    Object currentDoc;
    Object newParentDoc;

    if (!contentBeanInformation.getAspectDocumentName().equals(ContentBean.DOC_TYPE_ASPECT_DISABLED)) {
      // ououu we got an aspect, fancy stuff
      final Import doctypeImportReference = objectFactory.createImport();
      doctypeImportReference.setName(contentBeanInformation.getAspectDocumentName());

      DocTypeAspect currentDocTypeAspect = objectFactory.createDocTypeAspect();
      currentDocTypeAspect.setTargetType(doctypeImportReference);

      // done collecting this doctypes information
      knownDoctypes.put(doctypeImportReference.getName(), currentDocTypeAspect);
      currentDoc = currentDocTypeAspect;
      newParentDoc = doctypeImportReference;
    } else {
      // ContentBeanInformation contains information for a one DocType
      DocType currentDocType = objectFactory.createDocType();
      currentDocType.setName(contentBeanInformation.getDocumentName());

      //set abstract doctypes to abstract
      //not done for non abstract since it is the default
      if (contentBeanInformation.isAbstract()) {
        currentDocType.setAbstract(true);
      }
      // Current DocType may have parents
      if (parentDocType != null) {
        currentDocType.setParent(parentDocType);
      }
      // done collecting this doctypes information
      knownDoctypes.put(currentDocType.getName(), currentDocType);
      currentDoc = currentDocType;
      newParentDoc = currentDoc;
    }


    // Will collect current DocType and all child doctypes or doctypeaspects
    List<Object> docTypes = new LinkedList<Object>();
    docTypes.add(currentDoc);

    // add children recursively to docTypes
    SortedSet<ContentBeanInformation> childBeanInformationsSorted = new TreeSet<ContentBeanInformation>(
            new Comparator<ContentBeanInformation>() {
              @Override
              public int compare(ContentBeanInformation o1, ContentBeanInformation o2) {
                return o1.getDocumentName().compareTo(o2.getDocumentName());
              }
            });

    childBeanInformationsSorted.addAll(contentBeanInformation.getChilds());

    // extract children information now
    for (ContentBeanInformation cbi : childBeanInformationsSorted) {
      docTypes.addAll(extractChildDocTypes(cbi, newParentDoc));
    }

    return docTypes;
  }

  private void extractProperties(ContentBeanInformation contentBeanInformation, Set<? extends PropertyInformation> parentProperties) {
    // doctype or doctypeaspect
    DocType currentDocType = null;
    DocTypeAspect currentDocTypeAspect = null;
    if (!contentBeanInformation.getAspectDocumentName().equals(ContentBean.DOC_TYPE_ASPECT_DISABLED)) {
      currentDocTypeAspect = (DocTypeAspect) knownDoctypes.get(contentBeanInformation.getAspectDocumentName());
    } else {
      currentDocType = (DocType) knownDoctypes.get(contentBeanInformation.getDocumentName());
    }

    // add Properties
    SortedSet<PropertyInformation> propertyInformationsSorted = new TreeSet<PropertyInformation>(
            new Comparator<PropertyInformation>() {
              @Override
              public int compare(PropertyInformation o1, PropertyInformation o2) {
                return o1.getDocumentTypePropertyName().compareToIgnoreCase(o2.getDocumentTypePropertyName());
              }
            });

    propertyInformationsSorted.addAll(contentBeanInformation.getProperties());

    for (PropertyInformation propertyInformation : propertyInformationsSorted) {

      // filter out properties provided by parent, if is substantially different, override instead
      boolean isOverride = false;
      boolean skip = false;
      for (PropertyInformation parentProperty : parentProperties) {
        if (parentProperty.equals(propertyInformation)) {
          skip = true;
          break;
        } else if (parentProperty.getDocumentTypePropertyName().equals(propertyInformation.getDocumentTypePropertyName())) {
          // names are the same, but method signature is different
          // -> override if not doctypeaspect, skip if it is one!
          if (currentDocType != null) {
            isOverride = true;
            break;
          } else {
            skip = true;
            break;
          }
        }
      }
      if (skip) {
        continue;
      }


      // XML property descriptor to create
      Object element = createPropertyDescriptionFromPropertyInformation(propertyInformation);

      if (isOverride) {
        if (element instanceof JAXBElement) {
          ((JAXBElement<? extends Propertydescriptor>) element).getValue().setOverride(true);
        } else {
          ((Propertydescriptor) element).setOverride(true);
        }
      }

      if (element != null) {
        // doctype or doctypeaspect
        if (currentDocType != null) {
          currentDocType.getBlobPropertyOrDatePropertyOrIntProperty().add(element);
        } else {
          currentDocTypeAspect.getBlobPropertyOrDatePropertyOrIntProperty().add(element);
        }
      }
    }


    // merge with parent properties, then pass on properties
    if (!parentProperties.isEmpty()) {
      propertyInformationsSorted.addAll(parentProperties);
    }
    // recursive call for each child
    for (ContentBeanInformation contentBeanInformationChild : contentBeanInformation.getChilds()) {
      extractProperties(contentBeanInformationChild, propertyInformationsSorted);
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
    } else {
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

    // target type, if DocType: great, if DocTypeAspect: get the actual target type, i.e. the import statement
    Object value = knownDoctypes.get(docTypeName);
    if (value instanceof DocTypeAspect) {
      value = ((DocTypeAspect) value).getTargetType();

    }
    listProperty.setLinkType(value);

    listProperty.setMin(BigInteger.valueOf(propertyInformation.getMin()));
    listProperty.setMax(BigInteger.valueOf(propertyInformation.getMax()));
    return listProperty;
  }

  public Map<String, GrammarInformation> getFoundMarkupSchemaDefinitions() {
    return foundMarkupSchemaDefinitions;
  }

  public String getDoctypeTitle() {
    return doctypeTitle;
  }

  public void setDoctypeTitle(String doctypeTitle) {
    this.doctypeTitle = doctypeTitle;
  }
}
