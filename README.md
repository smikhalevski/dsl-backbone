# DSL Backbone [![Build Status](https://travis-ci.org/smikhalevski/dsl-backbone.png?branch=master)](https://travis-ci.org/smikhalevski/dsl-backbone)

Handy approach to create your own Java and XML-based [fluent][1] [domain specific languages][2].

## Contents

1. [DSL Authoring](#dsl-authoring)
    1. [Basic Java DSL](#basic-java-dsl)
    2. [JAXB Serialization](#jaxb-serialization)
    3. [Spring XML Context Integration](#spring-xml-context-integration)
        1. [Namespace Handler](#namespace-handler)
        2. [Generating XML Schema](#generating-xml-schema)
        3. [Registering Handler and XML Schema](#registering-handler-and-xml-schema)
    4. [Tag Context](#tag-context)
    5. [Extentions](#extentions)
2. [Features](#features) 
3. [Roadmap](#roadmap)
4. [Dependencies](#dependencies)
5. [License](#license)

## DSL Authoring

Couple of quick examples of what DSL Backbone can do.

Fluent internal [**Java DSL**](#basic-java-dsl):
```java
Car car = new Car()
        .id("my-car")
        .engine() // Enter Engine.class context.
            .id("M28.01")
            .gears(5)
            .end() // Jump back to Car.class context.
        .attribute("brand", "Porsche") // Add custom attribute.
        .attribute("color", "black")
```

<a name="spring-dsl-example"></a>Same configuration in terms of [**DSL for Spring XML**](#spring-xml-context-integration) context:
```xml
<?xml version="1.0" encoding="utf-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
            http://example.org/ http://example.org/car.xsd">
    <car xmlns="http://example.org/" id="my-car" brand="Porsche">
        <engine id="M28.01" gears="5"/>
    </car>
</beans>
```

### Basic Java DSL

DSL is considered to consist of tags just like XML. Tags encapsulate functionality context and are regular Java classes which must implement `org.ehony.dsl.api.Tag` interface.

Easiest way to create new tag is to extend `org.ehony.dsl.BaseTag` which describes tag with no nested tags:
```java
package org.example;
// Imports omitted.
@XmlType
class Engine extends BaseTag<Engine, Car> {

    @XmlAttribute
    public Integer gears;

    public Engine gears(int gears) {
        this.gears = gears;
        return this; // Method chaining is now possible. 
    }
}
```

This class represents `Engine` tag which can be a child of `Car` tag. Note that `Engine.gears(int)` method returns `this` allowing end user to [chain method invocations](http://en.wikipedia.org/wiki/Method_chaining) of `Engine` class.

You can omit `javax.xml.bind.annotation.*` annotations if you are not planning to [store your DSL in XML format](#jaxb-serialization) or use define them in [Spring XML context](#spring-xml-context-integration).

In order to hold child tags, `Car` tag must implement `org.ehony.dsl.ContainerTag` interface. This interface provides support of addition, removal, replacement of child tags and also allows to implement custom child tag reconfiguration strategy after overriding of `ContainerTag.configureChild(Tag)`.
```java
package org.example;
// Imports omitted.
@XmlType(name = "car")
public class Car extends ContainerBaseTag<Car, ContainerTag> {

    @XmlElement
    public Engine engine;

    public Engine engine() {
        if (engine == null) {
            // Car must have only one engine.
            engine = new Engine();
            appendChild(engine);
        }
        return engine;
    }
}
```

`ContainerBaseTag.appendChild(Tag)` simplifies appending of new children to container. If provided tag is already a child of container it is added to, then it is moved to the end of the list of children. If provided tag is the child of another container it is first detached from its previous parent and then attached to this container.

Engine can also be set as a child in more explicit way: `car.getChildren().add(engine)` or even like this `engine.setParent(this)`.

Tag context handling, additional attributes, parent-child relationships and other low-level stuff are handled under-the-hood via `BaseTag` and `ContainerBaseTag` implementations.

### JAXB Serialization

To enable XML serialization add `package-info.java` to `org.example` package:
```java
@XmlSchema(namespace = "http://example.org/")
package org.example;
import javax.xml.bind.annotation.XmlSchema;
```

How to store `Car` instance in XML format:
```java
import javax.xml.bind.*;

JAXBContext context = JAXBContext.newInstance(Car.class);
Marshaller m = context.createMarshaller();
m.marshal(customer, System.out);
```

Output (manually formatted):
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<car xmlns="http://example.org/" id="my-car" brand="Porsche" color="black">
    <engine id="M28.01" gears="5"/>
</car>
```

### Spring XML Context Integration

Several steps are required to add support of custom DSL tags in Spring XML context:

1. Add JAXB annotations to enable serialization support as [described above](#jaxb-serialization).
2. Create Spring namespace handler for DSL root tags.
3. [Create XML schema](#generating-xml-schema) describing your DSL tags.
4. Add `spring.handlers` and `spring.schemas` to `META-INF` folder to register namespace handler and created shema.

After all these steps you can retrieve `Car` instance from Spring context:
```java
applicationContext.getBean("my-car", car.class);
```

#### Namespace Handler

To add `Car` [custom bean definition support](#spring-dsl-example) to Spring context use:
```java
package org.example.spring;

import org.example.Car;
import org.ehony.dsl.spring.TagBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class CarNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("car", new TagBeanDefinitionParser(Car.class));
    }
}
```

`TagBeanDefinitionParser` incapsulates all means of bean deserialisation and instantination.

To create custom namespace handler, please refer to [Spring documentation](http://docs.spring.io/spring/docs/2.5.5/reference/extensible-xml.html#extensible-xml-namespacehandler).

#### Generating XML Schema

To generate XML schema automatically from your classes I recommend to use `org.codehaus.mojo:jaxb2-maven-plugin` Maven plugin. Note that schema would not contain any references to `BaseTag` or `ContainerBaseTag` because they are marked as `@XmlTransient` to allow usage of `@XmlValue` annotation on their subclasses.

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>jaxb2-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>schemagen</goal>
            </goals>
            <phase>generate-resources</phase>
            <configuration>
                <includes>
                    <include>org/example/*.java</include>
                </includes>
                <outputDirectory>${project.build.outputDirectory}</outputDirectory>
                <transformSchemas>
                    <transformSchema>
                        <uri>http://example.org/</uri>
                        <toFile>car.xsd</toFile>
                    </transformSchema>
                </transformSchemas>
            </configuration>
        </execution>
    </executions>
</plugin>
```

To allow `Car` tag deserialization from XML you should create `org/example/jaxb.index` with a single word `Car` or create a custom `ObjectFactory` class.

#### Registering Handler and XML Schema

Register namespace handler via creating `META-INF/spring.handlers`:
```
http\://example.org/=org.example.spring.CarNamespaceHandler
```

Add schema binding to `META-INF/spring.schemas`:
```
http\://example.org/car.xsd=car.xsd
```

Note that you must escape `:` character with a backslash `\`.

For more information refer to [Spring documentation](http://docs.spring.io/spring/docs/2.5.5/reference/extensible-xml.html#extensible-xml-registration).

### Tag Context

Tags have bound instance of `org.ehony.dsl.api.TagContext` and may share same beans in their life cycle.
```java
BasicTagContext context = new BasicTagContext();
context.bean("my-string", "Test"); // Registering new bean.
car.setContext(context);
```

Now all descendant tags of `car` instance can access bean `myBean` of type `java.lang.String`.
```java
Engine engine = (Engine) car.getChildren().get(0);
engine.getContext().getBean("my-string", String.class);
```

This technique allows to inject various bean providers into your DSL API. For example, when bean was defined in Spring XML context it is supplied with `org.ehony.dsl.spring.SpringTagContext` which proxies original application context.

## Extentions

DSL Backbone is shipped with several extenders which allow speeding up development of common tasks, such as character encoding and context bean referencing. See `org.ehony.dsl.extenders` package for more info.

## Features

- OSGi compatible.
- Supports Java 6 and above.
- Integration with [Spring](http://spring.io).
- Serialization support out of the box.
- Single dependency (org.springframework:spring-context is optional).
- Fully documented.
- Allows to add validation and context-based tag auto-configuration.
- Add arbitrary attributes for your DSL tags and keep them transparently serialized.

## Roadmap

- Strategy oriented tag validation.
- Increase test coverage.

## Dependencies

Apache Maven dependency:
```xml
<dependency>
    <groupId>org.ehony</groupId>
    <artifactId>dsl-backbone</artifactId>
    <version>1.1</version>
</dependency>
```

For Spring context support you should explicitly add `org.springframework:spring-context` dependency. Currently DSL Backbone depends on Spring 3.2.4.RELEASE but works fine with versions starting above 2.5.

## License

The code is available under [MIT licence](LICENSE.txt).

[1]: http://en.wikipedia.org/wiki/Fluent_interface
[2]: http://www.javaworld.com/article/2077865
