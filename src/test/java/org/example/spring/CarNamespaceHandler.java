package org.example.spring;

import org.ehony.dsl.spring.*;
import org.example.*;
import org.springframework.beans.factory.xml.*;

import javax.xml.bind.annotation.*;

/**
 * See test/resources/META-INF/spring.handlers for mapping details.
 */
@XmlTransient
public class CarNamespaceHandler extends NamespaceHandlerSupport
{

    @Override
    public void init() {
        registerBeanDefinitionParser("car", new TagBeanDefinitionParser(Car.class));
    }
}
