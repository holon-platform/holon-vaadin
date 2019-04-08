# Holon Vaadin module

> Latest release: [5.2.4](#obtain-the-artifacts)

This is the __Vaadin 8+ UI__ module of the [Holon Platform](https://holon-platform.com), which represents the platform support for the [Vaadin](https://vaadin.com) web applications framework, focusing on the user interface components and data binding features.

The module main features are:

* A Java API to build (using _fluent_ builders), manage and use the web application UI components.
* Integration with the platform foundation architecture, such as the `Property` model and the `Datastore` API, the authentication, authorization and localization support.
* A powerful and easy to use `View` _navigator_ to manage the web application _virtual pages_
* __Spring__ and __Spring Boot__ integration

See the module [documentation](https://docs.holon-platform.com/current/reference/holon-vaadin.html) for details.

Just like any other platform module, this artifact is part of the [Holon Platform](https://holon-platform.com) ecosystem, but can be also used as a _stand-alone_ library.

See the [platform documentation](https://docs.holon-platform.com/current/reference) for further details.

## Other Vaadin platform versions

The Holon Platform Vaadin module si also available for:

* [Vaadin Flow (10+)](https://github.com/holon-platform/holon-vaadin-flow)
* [Vaadin 7](https://github.com/holon-platform/holon-vaadin7)

## Code structure

See [Holon Platform code structure and conventions](https://github.com/holon-platform/platform/blob/master/CODING.md) to learn about the _"real Java API"_ philosophy with which the project codebase is developed and organized.

## Getting started

### System requirements

The Holon Platform is built using __Java 8__, so you need a JRE/JDK version 8 or above to use the platform artifacts.

[Vaadin](https://vaadin.com) version __8.1 or higher__ is required.

### Releases

See [releases](https://github.com/holon-platform/holon-vaadin/releases) for the available releases. Each release tag provides a link to the closed issues.

#### 5.2.x release notes

See [What's new in version 5.2.x](https://docs.holon-platform.com/current/reference/holon-vaadin.html#WhatsNew52x) to learn about the new features and API operations of the 5.2 minor release.

### Obtain the artifacts

The [Holon Platform](https://holon-platform.com) is open source and licensed under the [Apache 2.0 license](LICENSE.md). All the artifacts (including binaries, sources and javadocs) are available from the [Maven Central](https://mvnrepository.com/repos/central) repository.

The Maven __group id__ for this module is `com.holon-platform.vaadin` and a _BOM (Bill of Materials)_ is provided to obtain the module artifacts:

_Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform.vaadin</groupId>
        <artifactId>holon-vaadin-bom</artifactId>
        <version>5.2.4</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Using the Platform BOM

The [Holon Platform](https://holon-platform.com) provides an overall Maven _BOM (Bill of Materials)_ to easily obtain all the available platform artifacts:

_Platform Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform</groupId>
        <artifactId>bom</artifactId>
        <version>${platform-version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Build from sources

You can build the sources using Maven (version 3.3.x or above is recommended) like this: 

`mvn clean install`

## Getting help

* Check the [platform documentation](https://docs.holon-platform.com/current/reference) or the specific [module documentation](https://docs.holon-platform.com/current/reference/holon-vaadin.html).

* Ask a question on [Stack Overflow](http://stackoverflow.com). We monitor the [`holon-platform`](http://stackoverflow.com/tags/holon-platform) tag.

* Report an [issue](https://github.com/holon-platform/holon-vaadin/issues).

* A [commercial support](https://holon-platform.com/services) is available too.

## Examples

See the [Holon Platform examples](https://github.com/holon-platform/holon-examples) repository for a set of example projects.

## Contribute

See [Contributing to the Holon Platform](https://github.com/holon-platform/platform/blob/master/CONTRIBUTING.md).

[![Gitter chat](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/holon-platform/contribute?utm_source=share-link&utm_medium=link&utm_campaign=share-link) 
Join the __contribute__ Gitter room for any question and to contact us.

## License

All the [Holon Platform](https://holon-platform.com) modules are _Open Source_ software released under the [Apache 2.0 license](LICENSE).

## Artifacts list

Maven _group id_: `com.holon-platform.vaadin`

Artifact id | Description
----------- | -----------
`holon-vaadin` | Core artifact
`holon-vaadin-navigator` | View navigation support
`holon-vaadin-spring` | __Spring__ integration 
`holon-vaadin-spring-boot` | __Spring Boot__ integration 
`holon-starter-vaadin` | __Spring Boot__ _starter_ 
`holon-starter-vaadin-undertow` | __Spring Boot__ _starter_ using Undertow as embedded servlet container
`holon-vaadin-bom` | Bill Of Materials
`documentation-vaadin` | Documentation
