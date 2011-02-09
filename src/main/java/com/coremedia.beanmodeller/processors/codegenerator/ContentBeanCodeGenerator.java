package com.coremedia.beanmodeller.processors.codegenerator;

import com.coremedia.beanmodeller.processors.ContentBeanInformation;
import com.coremedia.beanmodeller.processors.MavenProcessor;
import com.coremedia.beanmodeller.processors.PropertyInformation;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 01.02.11
 * Time: 17:49
 */
public class ContentBeanCodeGenerator extends MavenProcessor {

  private String packageName = "com.telekom.myproject";
  //TODO this is a silly name and needs a better alternative
  public static final String IMPL_SUFFIX = "BeanAccessorizor";

  public JCodeModel generateCode(Set<ContentBeanInformation> rootBeans) {
    getLog().info("Starting code generation for content beans.");
    JCodeModel contentBeanCodeModel = new JCodeModel();
    JPackage beanPackage = contentBeanCodeModel._package(packageName);
    for (ContentBeanInformation bean : rootBeans) {
      try {
        generateClass(beanPackage, bean, contentBeanCodeModel);
      }
      catch (JClassAlreadyExistsException e) {
        throw new IllegalStateException("Error handling must be implemented!", e);
      }
    }
    return contentBeanCodeModel;
  }

  private void generateClass(JPackage beanPackage, ContentBeanInformation bean, JCodeModel contentBeanCodeModel) throws JClassAlreadyExistsException {
    generateClass(beanPackage, bean, contentBeanCodeModel, new HashSet<PropertyInformation>());
  }

  private void generateClass(JPackage beanPackage, ContentBeanInformation bean, JCodeModel codeModel, Set<PropertyInformation> propertiesInTheHierarchySoFar) throws JClassAlreadyExistsException {

    //the content accessor class is derrived from the bean class
    Class parentClass = bean.getContentBean();
    if (getLog().isDebugEnabled()) {
      getLog().debug("Generating class for " + parentClass.getCanonicalName());
    }
    //generate the class
    JDefinedClass beanClass = beanPackage._class(bean.getName() + IMPL_SUFFIX);
    //no null check since extends(null) leas to java.lang.Object
    beanClass._extends(parentClass);
    //TODO this comment has to be better
    //add some javadoc
    JDocComment javaDoc = beanClass.javadoc();
    javaDoc.add("Content Accessor for " + bean);

    //create a new Set of the accumulated properties of this class
    Set<PropertyInformation> allMyProperties = new HashSet<PropertyInformation>(propertiesInTheHierarchySoFar);
    //collect the properties defined in this class
    for (PropertyInformation property : bean.getProperties()) {
      allMyProperties.add(property);
    }
    //generate getter for each property
    for (PropertyInformation property : allMyProperties) {
      generatePropertyMethod(beanClass, property, codeModel);
    }

    //and go down the hierarchy
    for (ContentBeanInformation childBean : bean.getChilds()) {
      generateClass(beanPackage, childBean, codeModel, allMyProperties);
    }

  }

  private void generatePropertyMethod(JDefinedClass beanClass, PropertyInformation propertyInformation, JCodeModel codeModel) {
    Method method = propertyInformation.getMethod();
    if (getLog().isDebugEnabled()) {
      getLog().debug("Generating method for " + method.toString());
    }

    //construct the correct modifiers
    int modifiers = 0;
    int abstractMethodModifiers = method.getModifiers();
    if (Modifier.isPrivate(abstractMethodModifiers)) {
      modifiers |= JMod.PRIVATE;
    }
    else if (Modifier.isProtected(abstractMethodModifiers)) {
      modifiers |= JMod.PROTECTED;
    }
    else if (Modifier.isPublic(abstractMethodModifiers)) {
      modifiers |= JMod.PUBLIC;
    }
    //make it final - don't know if it is good for anything
    modifiers |= JMod.FINAL;
    //create the method
    JMethod propertyMethod = beanClass.method(modifiers, method.getReturnType(), method.getName());
    //TODO this comment has to be better
    //generate some java doc for the method
    JDocComment javadoc = propertyMethod.javadoc();
    javadoc.add("Getter for " + propertyInformation);
    //create the call to the content object returning th neccessary information
    //TODO Calendar/Date is a tad more complicated
    //TODO Collection generics get lost - is that important?
    JInvocation getterCall = JExpr.invoke("getContent").invoke("get").arg(JExpr.lit(propertyInformation.getDocumentTypePropertyName()));
    //the return type is the same return type as specifid by the abstract method
    JType returnType = codeModel.ref(propertyInformation.getMethod().getReturnType());
    //and let the method return the value
    propertyMethod.body()._return(JExpr.cast(returnType, getterCall));
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    if (packageName.endsWith(".")) {
      this.packageName = packageName.substring(0, packageName.length() - 1);
    }
    else {
      //TODO do we nee further validations? -> InvalidArgumentException??
      this.packageName = packageName;
    }
  }

  public String getCanonicalGeneratedClassName(ContentBeanInformation beanInformation) {
    Class beanClass = beanInformation.getContentBean();
    return packageName + "." + beanClass.getSimpleName() + IMPL_SUFFIX;
  }
}
