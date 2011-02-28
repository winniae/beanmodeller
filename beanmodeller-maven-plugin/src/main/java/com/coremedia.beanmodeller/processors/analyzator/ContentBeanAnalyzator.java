package com.coremedia.beanmodeller.processors.analyzator;

import com.coremedia.beanmodeller.annotations.ContentBean;
import com.coremedia.beanmodeller.annotations.ContentProperty;
import com.coremedia.beanmodeller.processors.AbstractPropertyInformation;
import com.coremedia.beanmodeller.processors.BlobPropertyInformation;
import com.coremedia.beanmodeller.processors.ContentBeanAnalyzationException;
import com.coremedia.beanmodeller.processors.ContentBeanAnalyzer;
import com.coremedia.beanmodeller.processors.ContentBeanAnalyzerException;
import com.coremedia.beanmodeller.processors.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.DatePropertyInformation;
import com.coremedia.beanmodeller.processors.EmptyContentBeanInformation;
import com.coremedia.beanmodeller.processors.IntegerPropertyInformation;
import com.coremedia.beanmodeller.processors.LinkListPropertyInformation;
import com.coremedia.beanmodeller.processors.MarkupPropertyInformation;
import com.coremedia.beanmodeller.processors.MavenProcessor;
import com.coremedia.beanmodeller.processors.StringPropertyInformation;
import com.coremedia.beanmodeller.processors.UnknownPropertyInformation;
import com.coremedia.cap.common.Blob;
import com.coremedia.objectserver.beans.AbstractContentBean;
import com.coremedia.xml.Markup;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The implementation for the content bean analyzer
 */
public class ContentBeanAnalyzator extends MavenProcessor implements ContentBeanAnalyzer {
  private List<Class> beansToAnalyze = new LinkedList<Class>();
  private Set<ContentBeanInformation> rootBeanInformation = null;

  public static final int MAX_CONTENT_TYPE_LENGTH = 16;
  public static final int MAX_PROPERTY_LENGTH = 32;
  public static final String SCHEMA_DEFINITION_LOCATION = "/xml_schema_definitions/";

  private static final ContentBeanInformation propertyDefaultLinkListType = EmptyContentBeanInformation.getInstance();

  // this hash map is used to have a fast lookup if we already got a bean info for a certain class
  // or to access the found information fastly
  // ONLY to be used during analyzation
  private Map<Class, AnalyzatorContentBeanInformation> allFoundContentBeanInformation;

  private static final Set<Class> VALID_METHOD_RETURN_TYPES = new HashSet<Class>();

  {
    VALID_METHOD_RETURN_TYPES.add(Integer.class);
    VALID_METHOD_RETURN_TYPES.add(Date.class);
    VALID_METHOD_RETURN_TYPES.add(Calendar.class);
    VALID_METHOD_RETURN_TYPES.add(List.class);
    VALID_METHOD_RETURN_TYPES.add(String.class);
    VALID_METHOD_RETURN_TYPES.add(Markup.class);
    VALID_METHOD_RETURN_TYPES.add(Blob.class);
  }

  /**
   * TODO make it configurable from outside.
   */
  private int propertyDefaultStringLength = 32;
  private String propertyDefaultMarkupGrammar = MarkupPropertyInformation.COREMEDIA_RICHTEXT_GRAMMAR_NAME;
  private int propertyDefaultLinkListMin = 0;
  private int propertyDefaultLinkListMax = Integer.MAX_VALUE;

  /**
   * <p>Finds all candidate content beans that reside in a "packageName". Call this method
   * before calling analyzeContentBeanInformation() method, if you want Analyzer to search annotated
   * candidate content bean classes automatically. </p>
   *
   * @param packageName package name to search for candidate content bean classes, e.g., "com.coremedia"
   */
  public void findContentBeans(String packageName) {
    if (getLog().isDebugEnabled()) {
      getLog().debug("Searching for content beans in package " + packageName);
    }
    ClassPathContentBeanScanner scanner = new ClassPathContentBeanScanner();
    Set<Class> candidateCBs = scanner.findCandidateContentBeanClasses(packageName, getLog());
    if (getLog().isInfoEnabled()) {
      getLog().info("Found " + candidateCBs.size() + " beans in package " + packageName);
    }
    for (Class contentBean : candidateCBs) {
      this.addContentBean(contentBean);
    }
  }

  @Override
  public void addContentBean(Class bean) {
    if (getLog().isDebugEnabled()) {
      getLog().debug("Adding class " + bean.getCanonicalName() + " to the analysation list");
    }
    beansToAnalyze.add(bean);
  }

  public Set<ContentBeanInformation> getContentBeanRoots() throws ContentBeanAnalyzerException {
    if (rootBeanInformation == null) {
      throw new ContentBeanAnalyzerException(ContentBeanAnalyzationException.LIFECYCLE_VIOLATION);
    }
    return rootBeanInformation;
  }

  /**
   * writes to rootBeanInformation.
   */
  public void analyzeContentBeanInformation() throws ContentBeanAnalyzationException {
    //to mark that we have analyzed the beans we create the root bean information object
    rootBeanInformation = new HashSet<ContentBeanInformation>();

    getLog().debug("Starting content bean analyzation");

    //we first create an exception - perhaps we add errors later
    ContentBeanAnalyzationException potentialException = new ContentBeanAnalyzationException();

    // this hash map is used to have a fast lookup if we already got a bean info for a certain class
    // or to access the found information fastly
    // GLOBAL VARIABLE
    allFoundContentBeanInformation = new HashMap<Class, AnalyzatorContentBeanInformation>();

    extractBeanClassHierarchy(potentialException);

    getLog().info("Found " + allFoundContentBeanInformation.keySet().size() + " content beans");

    //from this point on we can assume that all content beans are found and rootContentBeans &
    // allFoundContentBeanInformation are properly filled.
    //so first of all let's see if there is a problem in the hierarchy
    checkBeanClassHierarchy(potentialException);

    extractDocTypeNames(potentialException);

    extractDocProperties(potentialException);

    //have there been errors in the analyzation?
    if (potentialException.hasErrors()) {
      //delete any analyzation result
      rootBeanInformation = null;
      //and throw the prepared exception
      throw potentialException;
    }
    getLog().info("Content bean analyzation successfully performed");
  }

  private void checkBeanClassHierarchy(ContentBeanAnalyzationException potentialException) {
    getLog().info("checking bean class hierarchy");
    Set<Class> visitedClasses = new HashSet<Class>(allFoundContentBeanInformation.size());
    for (ContentBeanInformation bean : allFoundContentBeanInformation.values()) {
      checkBeanClassHierarchy(potentialException, bean, visitedClasses);
    }
  }

  private void checkBeanClassHierarchy(ContentBeanAnalyzationException potentialException, ContentBeanInformation bean, Set<Class> visitedClasses) {
    if (getLog().isDebugEnabled()) {
      getLog().debug("checking bean class hierarchy for " + bean);
    }
    //first look if there is no problem in the hierarchy between bean and parent
    Class contentBean = bean.getContentBean();
    for (Class currentClass = contentBean; !currentClass.equals(AbstractContentBean.class) && !visitedClasses.contains(bean.getClass()); currentClass = currentClass.getSuperclass()) {
      if (getLog().isDebugEnabled()) {
        getLog().debug("checking class in hierarchy: " + currentClass);
      }
      //first of all note that we visited the class
      visitedClasses.add(currentClass);
      analyzeMethods(potentialException, contentBean, currentClass);
    }
  }

  private void analyzeMethods(ContentBeanAnalyzationException potentialException, Class contentBean, Class currentClass) {
    Method[] methods = currentClass.getDeclaredMethods();
    //it is a content bean if there is an annotation - we do not really care about details here
    boolean isContentBean = currentClass.getAnnotation(ContentBean.class) != null;
    for (Method method : methods) {
      Annotation methodAnnotation = method.getAnnotation(ContentProperty.class);
      boolean isValidPropertyMethod = isValidPropertyMethod(method);
      boolean hasValidReturnType = hasValidReturnType(method);
      if (methodAnnotation != null) {
        //bean properties must be abstract
        if (!isValidPropertyMethod) {
          potentialException.addError(contentBean, method.getName(), ContentBeanAnalyzationException.INVALID_PROPERTY_MESSAGE +
              "In bean " + currentClass.getCanonicalName() + "the method " + method.getName() + " is not valid." +
              ContentBeanAnalyzationException.VALID_METHOD_HINTS_MESSAGE
          );
        }
        // methods must have specific return types
        if (!hasValidReturnType) {
          potentialException.addError(contentBean, method.getName(), ContentBeanAnalyzationException.INVALID_RETURN_TYPES_MESSAGE + VALID_METHOD_RETURN_TYPES);
        }
        //bean properties only in content beans
        if (!isContentBean) {
          potentialException.addError(contentBean, ContentBeanAnalyzationException.PROPERTY_NOT_IN_CB_MESSAGE +
              "In bean " + currentClass.getCanonicalName() + "the method " + method.getName() + " is marked as bean property but the class");
        }
        //TODO in the end we should notice somehow that we analyzed it & that it was marked positive
      }
      else {
        //we want to look at abstract methods only
        if (Modifier.isAbstract(method.getModifiers())) {
          // no annotation
          // don't be so strict.. if it passes "isValidPropertyMethod" then "hasValidReturnType" must pass too, though.
          if (!isValidPropertyMethod) {
            getLog().info("Method " + method + " has been ignored since it is no valid property method" +
                ContentBeanAnalyzationException.VALID_METHOD_HINTS_MESSAGE);
          }
          if (isValidPropertyMethod && !hasValidReturnType) {
            potentialException.addError(contentBean, method.getName(), ContentBeanAnalyzationException.INVALID_RETURN_TYPES_MESSAGE + VALID_METHOD_RETURN_TYPES);
          }
        }
        else {
          getLog().debug("Method " + method.getName() + " has been ignored since only abstract methods are considered.");
        }
        //TODO in the end we should notice somehow that we analyzed it & that it was marked positive
      }
    }
  }

  private void extractBeanClassHierarchy(ContentBeanAnalyzationException potentialException) {
    getLog().info("Extracting bean hierarchy");
    for (Class bean : beansToAnalyze) {
      //all content bean must extend AbstractContentBean – if not we must mark is as an error
      if (!(AbstractContentBean.class.isAssignableFrom(bean))) {
        potentialException.addError(bean, ContentBeanAnalyzationException.NOT_INHERITING_ABSTRACT_CONTENT_BEAN_MESSAGE + AbstractContentBean.class.getCanonicalName());
        break;
      }
      //ok it is a content bean lets see if we can find any of its ancessors in our list
      AnalyzatorContentBeanInformation lastBeanInformation = null;
      //we go up the hierarchy until we hit the abstract content bean
      for (Class currentClass = bean; !AbstractContentBean.class.equals(currentClass); currentClass = currentClass.getSuperclass()) {
        if (getLog().isDebugEnabled()) {
          getLog().debug("Extracting hierarchy from " + currentClass.getCanonicalName());
        }
        //is it a content bean?
        ContentBean beanAnnotation = (ContentBean) currentClass.getAnnotation(ContentBean.class);
        if (beanAnnotation != null) {
          //have we already seen it?
          AnalyzatorContentBeanInformation contentBeanInformation = allFoundContentBeanInformation.get(currentClass);
          // if we have not seen the class create a content bean information
          if (contentBeanInformation == null) {
            getLog().info("Found content bean " + currentClass.toString());
            contentBeanInformation = new AnalyzatorContentBeanInformation(currentClass);
            //and we save the information that we have a bean infor for that class
            allFoundContentBeanInformation.put(currentClass, contentBeanInformation);
          }
          //if it is in a hierarchy we have to model the hierarchy in the bean information
          if (lastBeanInformation != null) {
            lastBeanInformation.setParent(contentBeanInformation);
          }
          //since we are going upwards we have to remember the bean information, since we have to set the parent is we find another content bean in the hierarchy
          lastBeanInformation = contentBeanInformation;
        }
      }
      if (lastBeanInformation != null) {
        rootBeanInformation.add(lastBeanInformation);
      }
    }

  }

  private void extractDocTypeNames(ContentBeanAnalyzationException potentialException) {
    Set<Class> classesToAnalyze = allFoundContentBeanInformation.keySet();
    Map<String, Class> foundDocTypeNames = new HashMap<String, Class>();

    //we simply go through all classes and check & update all bean info
    for (Class classToAnalyze : classesToAnalyze) {
      AnalyzatorContentBeanInformation beanInformation = allFoundContentBeanInformation.get(classToAnalyze);

      //get our content bean annotation
      ContentBean beanAnnotation = (ContentBean) classToAnalyze.getAnnotation(ContentBean.class);

      //and create the content bean name either from the annotation or the class name
      String docTypeName;
      if (beanAnnotation.doctypeName().equals(ContentBean.DOC_TYPE_NAME_USE_CLASS_NAME)) {
        if (getLog().isDebugEnabled()) {
          getLog().debug("Using class name as content type name for " + classToAnalyze.getCanonicalName() + " since it is the default");
        }
        docTypeName = classToAnalyze.getSimpleName();
      }
      else {
        docTypeName = beanAnnotation.doctypeName();
        if (getLog().isDebugEnabled()) {
          getLog().debug("Using annotation name " + docTypeName + " as content type name for " + classToAnalyze.getCanonicalName() + ".");
        }

      }

      //check if the doctpye name name is too long
      if (docTypeName.length() > MAX_CONTENT_TYPE_LENGTH) {
        potentialException.addError(classToAnalyze, ContentBeanAnalyzationException.CLASSNAME_TOO_LOGN_FOR_DOCTPYENAME_MESSAGE + classToAnalyze.getCanonicalName()
            + " (the name \'" + docTypeName + "\' is " + docTypeName.length() + " - max is " + MAX_CONTENT_TYPE_LENGTH + ").");
        // and ignore property
        break;
      }
      //check if we have seen this doctype name before
      Class otherClass = foundDocTypeNames.get(docTypeName);
      if (otherClass != null) {
        potentialException.addError(classToAnalyze, ContentBeanAnalyzationException.DUPLICATE_DOCTYPE_NAMES_MESSAGE
            + classToAnalyze.getCanonicalName() + " and " + otherClass.getCanonicalName()
            + " use both " + docTypeName + " as document type name");
        //and ignore this property
        break;
      }
      //if everything is ok we have to remember that this class uses that doctype name
      //for the above check
      foundDocTypeNames.put(docTypeName, classToAnalyze);
      //and set the bean information
      beanInformation.setDocumentName(docTypeName);
    }
  }

  private void extractDocProperties(ContentBeanAnalyzationException potentialException) {
    Set<Class> classesToAnalyze = allFoundContentBeanInformation.keySet();

    //we simply go through all classes and check & update all bean info
    for (Class classToAnalyze : classesToAnalyze) {
      AnalyzatorContentBeanInformation beanInformation = allFoundContentBeanInformation.get(classToAnalyze);

      if (getLog().isDebugEnabled()) {
        getLog().debug("Analyzing methods for " + classToAnalyze.getCanonicalName());
      }

      // stores propertyNames for each class to prevent double property declarations
      Set<String> foundPropertyNames = new HashSet<String>();

      // fill filteredMethods with all abstract methods for this class complying to the rules
      Set<Method> filteredMethods = new HashSet<Method>();
      //TODO sollten wir uns eigentlich nicht nur auf schon analysierte Methoden beschränken
      for (Method method : classToAnalyze.getDeclaredMethods()) {
        if (isValidPropertyMethod(method) && hasValidReturnType(method)) {
          filteredMethods.add(method);
          if (getLog().isDebugEnabled()) {
            getLog().debug("Found property for " + classToAnalyze.getCanonicalName() + ": " + method.getName());
          }
        }
        else {
          if (getLog().isDebugEnabled()) {
            if (!isValidPropertyMethod(method)) {
              getLog().debug("Ignoring method " + classToAnalyze.getCanonicalName() + ", " + method.getName() + " since it is no real property method");
            }
            if (!hasValidReturnType(method)) {
              getLog().debug("Ignoring method " + classToAnalyze.getCanonicalName() + ", " + method.getName() + " since it has not the correct return type");
            }
          }
        }
        // don't raise an error for filtered methods, as this is handled earlier already
      }

      // create property information for each abstract method
      for (Method method : filteredMethods) {
        // property information
        AbstractPropertyInformation propertyInformation;

        String documentTypePropertyName = getDocumentTypePropertyNameFromMethod(method);

        // VALIDATE
        //check if the property name name is too long
        if (documentTypePropertyName.length() > MAX_PROPERTY_LENGTH) {
          potentialException.addError(classToAnalyze, documentTypePropertyName, ContentBeanAnalyzationException.METHODNAME_TOO_LOGN_FOR_DOCTPYENAME_MESSAGE + "max is " + MAX_PROPERTY_LENGTH);
          //we ignore this method
          break;
        }
        //check if we have seen this property name before
        if (foundPropertyNames.contains(documentTypePropertyName)) {
          potentialException.addError(classToAnalyze, documentTypePropertyName, ContentBeanAnalyzationException.DUPLICATE_PROPERTY_NAMES_MESSAGES);
          //and ignore this method
          break;
        }

        // SWITCH FOR EACH RETURN TYPE

        try {
          propertyInformation = getPropertyInformationForMethod(method);
        }
        catch (Exception e) {
          potentialException.addError(classToAnalyze, documentTypePropertyName, e.getMessage());

          // don't include this method in result
          break;
        }

        // POST CHECK
        if (propertyInformation instanceof UnknownPropertyInformation) {
          potentialException.addError(classToAnalyze, documentTypePropertyName, ContentBeanAnalyzationException.PROPERTY_RETURN_TYPE_UNKNOWN_MESSAGE + method.getReturnType());

          // don't include this method in result
          break;
        }

        propertyInformation.setDocumentTypePropertyName(documentTypePropertyName);

        // save property name for validation of duplicate entries in next iteration for each class
        foundPropertyNames.add(propertyInformation.getDocumentTypePropertyName());

        // add propertyInformation to contentBeanInformation
        beanInformation.addProperty(propertyInformation);
      }
    }
  }

  /**
   * Throws error if there are analyzation errors.
   *
   * @param method to analyze
   * @return PropertyInformation for this error.
   * @throws Exception analyzation error occurred. Exception message should be included to "potentialException" list.
   */
  private AbstractPropertyInformation getPropertyInformationForMethod(Method method) throws Exception {
    // switch over return type
    final Class<?> returnType = method.getReturnType();

    if (returnType.equals(Integer.class)) {
      return new IntegerPropertyInformation(method);
    }
    else if (returnType.equals(Calendar.class) || returnType.equals(Date.class)) {
      return new DatePropertyInformation(method);
    }
    else if (returnType.equals(Markup.class)) {
      return getMarkupPropertyInformation(method);
    }
    else if (returnType.equals(String.class)) {
      return getStringPropertyInformation(method);
    }
    else if (returnType.equals(List.class)) {
      return getLinkListPropertyInformation(method);
    }
    else if (allFoundContentBeanInformation.containsKey(returnType)) {
      return getLinkListPropertyInformationSingle(method, allFoundContentBeanInformation.get(returnType));
    }
    else if (returnType.equals(Blob.class)) {
      return getBlobPropertyInformation(method);
    }
    else {
      // default
      return new UnknownPropertyInformation(method);
    }
  }

  private AbstractPropertyInformation getBlobPropertyInformation(Method method) throws ContentBeanAnalyzerException {
    ContentProperty annotation = method.getAnnotation(ContentProperty.class);
    String mimeTypeName = (annotation != null) ? annotation.propertyBlobMimeType() : ContentProperty.BLOB_PROPERTY_DEFAULT_MIME_TYPE;
    try {
      MimeType mimetype = new MimeType(mimeTypeName);
    }
    catch (MimeTypeParseException e) {
      throw new ContentBeanAnalyzerException(ContentBeanAnalyzationException.INVALID_MIME_TYPE_MESSAGE + mimeTypeName + " is not a valid mime type (it should have the pattern X/Y");
    }

    BlobPropertyInformation blobPropertyInformation = new BlobPropertyInformation(method);
    blobPropertyInformation.setAllowedMimeTypes(mimeTypeName);
    return blobPropertyInformation;
  }

  /**
   * Create PropertyInformation for a LinkList containing at most 1 item.
   *
   * @param method     Method to create the PropertyInformation for
   * @param linkedBean Object of the ContentBeanInformation that is referenced by the Link.
   * @return PropertyBeaninformation
   */
  private AbstractPropertyInformation getLinkListPropertyInformationSingle(Method method, AnalyzatorContentBeanInformation linkedBean) {
    LinkListPropertyInformation linkListPropertyInformation = new LinkListPropertyInformation(method);
    linkListPropertyInformation.setLinkType(linkedBean);
    linkListPropertyInformation.setMax(1);

    // todo read from annotation
    linkListPropertyInformation.setMin(propertyDefaultLinkListMin);

    return linkListPropertyInformation;
  }

  private AbstractPropertyInformation getLinkListPropertyInformation(Method method) throws Exception {
    LinkListPropertyInformation linkListPropertyInformation = new LinkListPropertyInformation(method);

    // todo read from annotation
    linkListPropertyInformation.setMin(propertyDefaultLinkListMin);
    linkListPropertyInformation.setMax(propertyDefaultLinkListMax);

    // read Linktype from return type of the method
    Type genericReturnType = method.getGenericReturnType();

    if (genericReturnType instanceof ParameterizedType) {
      // return Type is parameterized -> an explicit type is given
      // whoo, quite optimistic cast!?
      Class returnTypeLinkType = (Class) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];

      // find the ContentBeanInformation that represents this LinkType
      ContentBeanInformation contentBeanInformationLinkType = allFoundContentBeanInformation.get(returnTypeLinkType);

      // ## validate
      if (contentBeanInformationLinkType == null) {
        throw new Exception(ContentBeanAnalyzationException.LINKED_DOCTYPE_UNKNOWN + returnTypeLinkType);
      }

      linkListPropertyInformation.setLinkType(contentBeanInformationLinkType);
    }
    else {
      // no explicit type for linktype given -> use the default
      linkListPropertyInformation.setLinkType(propertyDefaultLinkListType);
    }

    return linkListPropertyInformation;
  }

  private AbstractPropertyInformation getStringPropertyInformation(Method method) throws Exception {
    // method annotation
    final ContentProperty methodAnnotation = method.getAnnotation(ContentProperty.class);

    // ## VALIDATE
    int stringLength;
    if (methodAnnotation == null || methodAnnotation.stringLength() == ContentProperty.STRING_PROPERTY_DEFAULT_LENGTH) {
      // use property default value
      stringLength = getPropertyDefaultStringLength();
    }
    else {
      // it is defined and not the "marker default", use that value then, of course
      stringLength = methodAnnotation.stringLength();
    }

    // raise error if stringLength is negative
    if (stringLength < 0) {
      throw new Exception(ContentBeanAnalyzationException.STRING_PROPERTY_TOO_SHORT_MESSAGE + stringLength);
    }

    StringPropertyInformation stringPropertyInformation = new StringPropertyInformation(method);
    stringPropertyInformation.setLength(stringLength);

    return stringPropertyInformation;
  }

  private AbstractPropertyInformation getMarkupPropertyInformation(Method method) throws Exception {
    // method annotation
    final ContentProperty methodAnnotation = method.getAnnotation(ContentProperty.class);
    String grammarName;
    URL grammarURL = null;
    String grammarRoot = null;

    if (methodAnnotation == null || ContentProperty.MARKUP_PROPERTY_DEFAULT_GRAMMAR.equals(methodAnnotation.propertyXmlGrammar())) {
      grammarName = getPropertyDefaultMarkupGrammar();
    }
    else {
      grammarName = methodAnnotation.propertyXmlGrammar();
      grammarURL = getClass().getResource(SCHEMA_DEFINITION_LOCATION + grammarName);
      grammarRoot = methodAnnotation.propertyXmlRoot();
      if (grammarURL == null) {
        throw new Exception(ContentBeanAnalyzationException.SCHEMA_DEFINITION_NOT_FOUND_MESSAGE + grammarName + ". It is expected to reside in folder " + SCHEMA_DEFINITION_LOCATION);
      }
      else if (ContentProperty.MARKUP_PROPERTY_NO_ROOT_SET.equals(grammarRoot)) {
        throw new Exception(ContentBeanAnalyzationException.SCHEMA_NO_XML_ROOT_SET_MESSAGE + " " + grammarName);
      }
    }

    final MarkupPropertyInformation markupInformation = new MarkupPropertyInformation(method);
    markupInformation.setGrammarName(grammarName);
    markupInformation.setGrammarURL(grammarURL);
    markupInformation.setGrammarRoot(grammarRoot);

    return markupInformation;
  }

  /**
   * Just calculate the name. Take method name or annotation, if available. Method name definitions are stripped of "is" and "get" prefixes.
   *
   * @param method method to calculate name for
   * @return documenttypeproperty name
   */
  private String getDocumentTypePropertyNameFromMethod(Method method) {
    final ContentProperty methodAnnotation = method.getAnnotation(ContentProperty.class);
    String documentTypePropertyName = method.getName().startsWith("is") ? method.getName().substring(2) : method.getName().substring(3);

    // get method name from Annotation if available.
    if (methodAnnotation != null && !methodAnnotation.propertyName().equals(ContentProperty.PROPERTY_NAME_USE_METHOD_NAME)) {
      // if annotation is not set to default (extract from method name), then set property name from annotation
      documentTypePropertyName = methodAnnotation.propertyName();
    }
    return documentTypePropertyName;
  }

  private boolean isValidPropertyMethod(Method method) {
// methods must be:
    // - abstract
    // - public or protected
    // - start with "get" or "is"
    // - have no parameters
    if (!Modifier.isAbstract(method.getModifiers())) {
      return false;
    }
    if (!Modifier.isProtected(method.getModifiers()) && !Modifier.isPublic(method.getModifiers())) {
      return false;
    }
    boolean isBoolean = Boolean.class.isAssignableFrom(method.getReturnType());
    String methodPrefix = (isBoolean) ? "is" : "get";
    if (!(method.getName().startsWith(methodPrefix))) {
      return false;
    }
    if (method.getParameterTypes().length > 0) {
      return false;
    }
    return true;
  }

  private boolean hasValidReturnType(Method method) {
    final Class<?> returnType = method.getReturnType();

    return !returnType.isPrimitive()
        && (VALID_METHOD_RETURN_TYPES.contains(returnType)
        || allFoundContentBeanInformation.containsKey(returnType));
  }


  public int getPropertyDefaultStringLength() {
    return propertyDefaultStringLength;
  }

  public void setPropertyDefaultStringLength(int propertyDefaultStringLength) {
    this.propertyDefaultStringLength = propertyDefaultStringLength;
  }

  public String getPropertyDefaultMarkupGrammar() {
    return propertyDefaultMarkupGrammar;
  }

  public void setPropertyDefaultMarkupGrammar(String propertyDefaultMarkupGrammar) {
    this.propertyDefaultMarkupGrammar = propertyDefaultMarkupGrammar;
  }
}
