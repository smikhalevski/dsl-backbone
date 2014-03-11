# Java DSL Backbone

Handy approach to create your own Java and XML-based [fluent][1] [domain specific languages][2].

**Contents**

1. [Example](#example)
2. [Serialization](#serialization)
3. [Tag Context](#tag-context)
4. [Features](#features)
5. [License](#license)

## Example

In terms of this library DSL consists of tags just like XML. Tags encapsulate functionality context and are regular Java classes which must implement `org.ehony.dsl.api.Tag` interface.

Easiest way to create new tag is to extend `org.ehony.dsl.BaseTag`:

```java
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

You can omit `javax.xml.bind.annotation.*` annotations if you are not planning to store your DSL in XML format.

In order to hold child tags, `Car` tag must be of type `org.ehony.dsl.ContainerTag` which is an interface that DSL tags implement to support addition, removal and replacement of child elements.

```java
public class Car extends ContainerBaseTag<Car, ContainerTag> {

    public Engine engine;

    public Engine engine() {
        if (engine == null) {
            // Car must have an engine.
            engine = appendChild(new Engine());
        }
        return engine;
    }
}
```

`ContainerBaseTag.appendChild(Tag)` simplifies appending of new children to container. If provided tag is already a child of container it is added to, then it is moved to the end of the list of children. If provided tag is the child of another container it is first detached from its previous parent and then attached to this container.

Fluent API is now ready to use:

```java
Car car = new Car()
        .id("my-car")
        .engine() // Enter engine context.
            .id("M28.01")
            .gears(5)
            .end() // Jump back to car context.
        .attribute("brand", "Porsche")
        .attribute("color", "black")
```

Tag context handling, additional attributes, parent-child relationships and other low-level stuff are handled under-the-hood.

## Serialization

To enable XML serialization add `package-info.java` to package with `Car` and `Engine` classes:

```java
@XmlSchema(namespace = "http://example.org/")
package org.example;
import javax.xml.bind.annotation.*;
```

Store `Car` instance in XML format:

```java
javax.xml.bind.JAXBContext.newInstance(Car.class).createMarshaller().marshal(car, System.out);
```

Output (manually formatted):

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<car xmlns="http://example.org/" id="my-car" brand="Porsche" color="black">
    <engine id="M28.01" gears="5"/>
</car>
```

## Tag Context

Tags have bound instance of `org.ehony.dsl.api.TagContext` and may share same beans in their life cycle.
 
```java
BasicTagContext context = new BasicTagContext();
context.registerBean("myBean", "Test");
car.setContext(context);
```

Now all descendant tags of `car` instance can access bean `myBean` of type `java.lang.String`.

```java
car.getChildren().get(0).getContext().getBean("myBean", String.class)
```

This allows to inject various bean providers into your API.

DSL Backbone is shipped with several extenders which allow speeding up development of common tasks, such as character encoding and context bean referencing. See `org.ehony.dsl.extenders` package for more info.

## Features

- OSGi compatible.
- Serialization support out of the box.
- Single dependency.
- Fully documented.
- Integration with [Spring](http://spring.io) and other bean containers.
- Easily add extensive validation.
- Implement specific tag configuration strategies depending on context.
- Add arbitrary attributes for your DSL tags and keep them transparently serialized.

## License

The code is available under [MIT licence](LICENSE.txt).

[1]: http://en.wikipedia.org/wiki/Fluent_interface
[2]: http://www.javaworld.com/article/2077865
