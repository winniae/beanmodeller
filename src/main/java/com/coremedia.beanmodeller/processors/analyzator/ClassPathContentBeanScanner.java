package com.coremedia.beanmodeller.processors.analyzator;

import com.coremedia.beanmodeller.annotations.ContentBean;
import org.apache.maven.plugin.logging.Log;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.HashSet;
import java.util.Set;

/**
 * Telekom .COM Relaunch 2011
 * User: aratas
 * Date: 01.02.2011
 * Time: 18:45:19
 */
public class ClassPathContentBeanScanner extends ClassPathScanningCandidateComponentProvider {


  /**
   * Create a ClassPathScanningCandidateComponentProvider.
   *
   * @param useDefaultFilters whether to register the default filters for the
   *                          {@link org.springframework.stereotype.Component @Component}, {@link org.springframework.stereotype.Repository @Repository},
   *                          {@link org.springframework.stereotype.Service @Service}, and {@link org.springframework.stereotype.Controller @Controller}
   *                          stereotype annotations
   * @see #registerDefaultFilters()
   */
  public ClassPathContentBeanScanner() {
    super(false);
  }

  /**
   * Determine whether the given bean definition qualifies as candidate content bean.
   */
  @Override
  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
    return (beanDefinition.getMetadata().isIndependent());
  }

  /**
   * Returns a set of all candidate beans. Candidate beans are any Java class that are marked with ContentBean annotation.
   * The class can be abstract, concrete or inner class.
   *
   * @param packageName package name where to search for contentbeans
   * @return
   */
  public Set<Class> findCandidateContentBeanClasses(String packageName, Log log) {
    // contains candidate content bean classes
    Set<Class> candidateBeanClasses = new HashSet<Class>();

    // this filter will take care that only "ContentBean" annotated classes will be found
    this.addIncludeFilter(new AnnotationTypeFilter(ContentBean.class));

    // delegate to find candidate components, which may be candidate beans
    Set<BeanDefinition> candidateComponents = this.findCandidateComponents(packageName);

    for (BeanDefinition bd : candidateComponents) {
      Class cls = null;
      try {
        cls = Class.forName(bd.getBeanClassName());
        candidateBeanClasses.add(cls);
      }
      catch (ClassNotFoundException e) {
        // ignore 
      }
    }

    return candidateBeanClasses;

  }

}
