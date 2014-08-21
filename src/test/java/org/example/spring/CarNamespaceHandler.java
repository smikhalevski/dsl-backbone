/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.example.spring;

import org.ehony.dsl.spring.TagBeanDefinitionParser;
import org.example.Car;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.lang.invoke.MethodHandles;

/**
 * For Spring custom mapping details refer to
 * <a href="http://docs.spring.io/spring/docs/3.2.4.RELEASE/spring-framework-reference/html/extensible-xml.html#extensible-xml-custom-nested">Spring documentation</a>
 * and <tt>test/resources/META-INF/spring.*</tt> files.
 */
public class CarNamespaceHandler extends NamespaceHandlerSupport
{

    @Override
    public void init() {
        registerBeanDefinitionParser("super-car", new TagBeanDefinitionParser(Car.class));
    }
}
