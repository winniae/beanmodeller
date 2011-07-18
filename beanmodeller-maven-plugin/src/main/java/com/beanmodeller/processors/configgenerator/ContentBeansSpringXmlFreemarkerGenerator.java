package com.coremedia.beanmodeller.processors.configgenerator;

import com.coremedia.beanmodeller.beaninformation.ContentBeanInformation;
import com.coremedia.beanmodeller.maven.PluginException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Telekom .COM Relaunch 2011
 * User: wmosler
 */
public class ContentBeansSpringXmlFreemarkerGenerator extends ContentBeansSpringXmlGenerator {

  @Override
  public void createSpringConfig(Set<ContentBeanInformation> roots) throws MojoFailureException {

    Template temp = null;

    try {
      // create template engine config
      Configuration cfg = new Configuration();
      // Specify the data source where the template files come from.
      // Here I set a file directory for it:
      cfg.setClassForTemplateLoading(ContentBeansSpringXmlFreemarkerGenerator.class, "/freemarker-templates/");
      // Specify how templates will see the data-model. This is an advanced topic...
      // but just use this:
      cfg.setObjectWrapper(new DefaultObjectWrapper());


      temp = cfg.getTemplate("contentbeans.xml.ftl");
    }
    catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }


    if (temp == null) {
      return;
    }

    // write to file
    try {
      final Writer writer = getTargetSpringConfigFile();

      // Create the root hash
      Map root = new HashMap();
      List contentbeans = new LinkedList();
      root.put("contentbeans", contentbeans);

      addFreemarkerContentBeansRecursive(roots, contentbeans);

      temp.process(root, writer);

      writer.flush();
      writer.close();
    }
    catch (PluginException e) {
    }
    catch (IOException e) {
    }
    catch (TemplateException e) {
      e.printStackTrace();
    }

  }

  private void addFreemarkerContentBeansRecursive(Set<? extends ContentBeanInformation> contentBeanInformations, List contentbeans) {
    SortedSet<ContentBeanInformation> beanInformationsSorted = sortContentBeanInformationSet(contentBeanInformations);

    // for each bean, write code and call recursive
    for (ContentBeanInformation contentBeanInformation : beanInformationsSorted) {
      if (getLog().isDebugEnabled()) {
        getLog().debug("Writing spring Configuration for " + contentBeanInformation);
      }
      Object contentbean = new FreemarkerContentBean(
          getBeanName(contentBeanInformation),
          getBeanName(contentBeanInformation.getParent()),
          contentBeanInformation.isAbstract() ? "" : getCodeGenerator().getCanonicalGeneratedClassName(contentBeanInformation));
      contentbeans.add(contentbean);

      addFreemarkerContentBeansRecursive(contentBeanInformation.getChilds(), contentbeans);
    }
  }

  public static class FreemarkerContentBean {
    private String name;
    private String parent;
    private String beanclass;

    public FreemarkerContentBean(String name, String parent, String beanclass) {
      this.name = name;
      this.parent = parent;
      this.beanclass = beanclass;
    }

    public String getName() {
      return name;
    }

    public String getParent() {
      return parent;
    }

    public String getBeanclass() {
      return beanclass;
    }
  }
}
