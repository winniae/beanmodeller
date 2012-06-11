# Bean Modeller

The Bean Modeller supports an incremental CoreMedia project development. It eases the effort of changing the Document Type Model (DTM). While critical changes can be done in Java code with minimal specification, Bean Modeller validates the specification and generates code that is usually error prone to maintain by hand.

Technically speaking, by annotating Java classes and (abstract) methods Bean Modeller generates the `doctype.xml`, the `contentbeans.xml` and UAPI content access codes with every `maven` build.

CoreMedia projects that utilize Bean Modeller have a more intuitive, cleaner and easier to maintain overall code structure.

## CHANGELOG

### 1.5

 * First public release.
 * Changed package to `com.coremedia.contribution.maven`

## Motivation

The usual way to describe Contentbeans is a CoreMedia XML specification, the contentbeans.xml. The CMS uses it to lay out its datastructure in a Database. The classic Beangenerator uses the same XML to generate Java Interfaces and a Base Implementation.

Developers are advised not to edit the Base Implementation but instead write business logic in a subclass of Base usually called Impl. The classic Beangenerator creates the Impl classes. The hierarchy then looks like:


    AInterface        BInterface
    ^                 ^
    ABase <- AImpl <- BBase <- BImpl


The Bean Modeller changes two things:

 * Beans are described as Java Annotations on existing classes.
 * The class hierarchy is reversed.

A developer creates a Java class representing his business object and logic. He defines abstract methods and annotates them with Bean Modeller specific Annotations. The Bean Modeller creates the Content Access code from the Annotation as a subclass of the abstract class.


    AInterface <- AAbstract <- AGenerated
    ^             ^
    BInterface <- BAbstract <- BGenerated


The generated code in BGenerated will have to also contain the code that is generated in AGenerated. But since the code is generated, this duplication does not cause any additional trouble, effort or problems.

## @github

https://github.com/wmosler/beanmodeller

## @CM Contributions Maven Repository

Releases: https://repository.coremedia.com/nexus/content/repositories/contributions-releases/com/coremedia/contribution/maven/

Snapshots: https://repository.coremedia.com/nexus/content/repositories/contributions-snapshots/com/coremedia/contribution/maven/

## Documentation

For the complete documentation, please clone the project from github and execute

    mvn site


## Basic Usage

 1. Annotate your ContentBean-class with `@ContentBean`.
 1. Make your ContentBean extend from `com.coremedia.objectserver.beans.AbstractContentBean`.
 1. Declare and configure the maven dependencies. See the generated site for details. Basically, the build-plugin `com.coremedia.contribution.maven.beanmodeller-maven-plugin` can be executed with the two goals `generate-doctypes` and `generate-contentbeans`.

Example:

    import com.coremedia.beanmodeller.annotations.ContentBean;
    import com.coremedia.beanmodeller.annotations.ContentProperty;
    import com.coremedia.objectserver.beans.AbstractContentBean;

    @ContentBean
    public abstract class Article extends AbstractContentBean {

      /**
       * Main text for the textual content.
       *
       * @return the main text
       */
      public abstract Markup getText();

      /**
       * Main image for the textual content.
       *
       * @return main image
       */
      @ContentProperty(propertyName = "image")
      protected abstract Media getImageInternal();

      /**
       * Get the linked image or the default image if the linked image is a media container
       *
       * @return default image or null
       */
      public Media getImage() {
        final Media image = getImageInternal();

        //image might be Image or MediaContainer object, get default image
        return image == null ? null : image.getMediaObject(ARTICLE_MEDIA_FORMAT_SMALL);
      }

      @ContentProperty(propertyName = "medias", propertyXmlGrammar = "classpath:xml_schema_definitions/xml-1998.xsd
          classpath:xml_schema_definitions/xlink-1999.xsd classpath:xml_schema_definitions/medias-1.0.xsd")
      abstract public Markup getMediasMarkup();
    }


## Project workspace structure and dependency management

At telekom.com, we have separate modules for our annotated abstract content beans and the generated concrete Accessorizors, that have the generated code to access CMS content via UAPI.

The root `pom.xml` configures the maven plugin to use the `contentbeans` module and look for annotated beans in the package `com.dtag.relaunchcom2011.develop.abstractbeans`.

    <build>
    <pluginManagement>
      <plugins>
        <plugin>
          <groupId>com.coremedia.contribution.maven</groupId>
          <artifactId>beanmodeller-maven-plugin</artifactId>
          <version>1.5</version>
          <configuration>
            <contentBeanPackage>com.dtag.relaunchcom2011.develop.abstractbeans</contentBeanPackage>
          </configuration>
          <dependencies>
            <dependency>
              <groupId>${project.groupId}</groupId>
              <artifactId>contentbeans</artifactId>
              <version>${project.version}</version>
            </dependency>
          </dependencies>
        </plugin>
        ....

Remember to configure the contributions repository as a maven pluginrepository as well.


The default `contentbeans` module only depends on the beanmodeller-annotations:

    <dependency>
      <groupId>com.coremedia.contribution.maven</groupId>
      <artifactId>beanmodeller-annotations</artifactId>
    </dependency>


The `contentbeans-accessorizors` module has no additional code, but only this build instruction and a dependency to the contentbeans module:

    <build>
    <plugins>
      <plugin>
        <groupId>com.coremedia.contribution.maven</groupId>
        <artifactId>beanmodeller-maven-plugin</artifactId>
        <executions>
          <execution>
            <id>generate-content-beans</id>
            <goals>
              <goal>generate-contentbeans</goal>
            </goals>
            <phase>generate-sources</phase>
            <configuration>
              <accessorizorBeansTargetPackage>com.dtag.relaunchcom2011.generatedbeans
              </accessorizorBeansTargetPackage>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
    </build>

The `contentbeans.xml` is generated together with the accessorizor beans. The default location is `target/classes/beanconfig/contentbeans.xml`.


The `contentserver-base` module generates the `doctypes.xml`:

    <build>
    <plugins>
      ...
      <!-- configuration of beanmodeller plugin -->
      <plugin>
        <groupId>com.coremedia.contribution.maven</groupId>
        <artifactId>beanmodeller-maven-plugin</artifactId>
        <executions>
          <execution>
            <id>generate-doctypes</id>
            <phase>generate-resources</phase>
            <goals>
              <goal>generate-doctypes</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
    </build>



## Bean Modeller Build & Release

The Bean Modeller itself is built with maven. The contributions repository is configured in the root pom. Snapshot builds are uploaded when you execute:

    mvn clean install deploy

Releases are performed and uploaded with:

    mvn release:prepare
    mvn release:perform

