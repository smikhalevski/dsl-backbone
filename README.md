# Ehony DSL

Handy approach to create Java and XML-based fluent domain specific languages.

## Fluent API Example

DSL consists of tags just like a regular XML.
To implement new simple DSL tag extend `org.ehony.dsl.BaseTag<Type, Parent>`:

```java
class Engine extends BaseTag<Engine, Car> {&hellip;}
```

Use [method chaining](http://en.wikipedia.org/wiki/Method_chaining) technique to create your fluent API:

```java
class Engine extends BaseTag<Engine, Car> {

    @XmlAttribute
    public Integer gears;

    public Engine gears(int gears) {
        this.gears = gears;
        return this; // Chaining is now possible. 
    }
}
```

Omit `javax.xml.bind.annotation` annotations if you are not planning to store your DSL in XML format.

`Parent` parameter of `BaseTag` must be of `org.ehony.dsl.ContainerTag` type.
`ContainerTag` is an interface that DSL tags must implement in order to support addition, removal and replacement of child tags.

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

`ContainerBaseTag.appendChild(Tag)` simplifies appending of another child tag to container.
If provided tag is already a child of container it is added to, then it is moved to the end of the list of children.
If provided tag is the child of another container it is first detached from its previous parent and then attached to this container.

Car [fluent API](http://en.wikipedia.org/wiki/Fluent_interface) is now ready to use:

```java
new Car()
        .id("my-car")
        .engine() // Enter engine tag context.
            .id("M28.01")
            .gears(5)
            .end() // Jump back to car context.
        .attribute("brand", "Porsche")
        .attribute("color", "black")
```

## XML Serialization

To enable serialization add `package-info.java` to package with `Car` and `Engine` classes:

```java
@XmlSchema(namespace = "http://example.org/", elementFormDefault = XmlNsForm.QUALIFIED)
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
package org.example;

import javax.xml.bind.annotation.*;
```

Store `Car` instance in an XML format:

```java
javax.xml.bind.JAXBContext.newInstance(Car.class).createMarshaller().marshal(car, System.out);
```

Output:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<car xmlns="http://example.org/" id="my-car" brand="Porsche" color="black">
    <engine id="M28.01" gears="5"/>
</car>
```

## Using Tag Context

Tags have bound instance of `TagContext` and may share same object in their life cycle.
 
```java
BasicTagContext context = new BasicTagContext();
context.registerBean("myBean", "Test");

car.setContext(context);
```

Now all descendant tags of `car` instance can access registered bean:

```java
car.engine().getContext().getBean("myBean", String.class)
```

This allows to inject various bean providers into your DSL.
See `org.ehony.dsl.extenders.BeanReferenceBaseTag` for more info on bean wiring.

## Features

- OSGi-compatible manifest and `features.xml`.
- XML serialization support out of the box.
- Single dependency.
- Fully documented.
- Easy integration with [Spring](http://spring.io) and other bean containers.
- Add unique identifiers with `BaseTag.id(String)`.
- Use `Tag#end()` method to jump back to parent tag context. For a [root tag](http://en.wikipedia.org/wiki/Root_element) this method returns `null`.  
- Add arbitrary attributes for your DSL tags with `BaseTag.attribute(QName, Object)`.
- Override `BaseTag.validate()` method to allow extensive validation.
- Implement specific child configuration strategy via overriding `ContainerBaseTag.configureChild(Tag)`.

## License

The code is available under [MIT licence](LICENSE.txt).
