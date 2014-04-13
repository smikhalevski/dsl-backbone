/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.spring;

import org.ehony.dsl.api.Tag;
import org.springframework.beans.factory.support.*;
import org.springframework.beans.factory.xml.*;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlTransient;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

/**
 * Bean definition parser that uses JAXB under-the-hood.
 */
@XmlTransient
public class TagBeanDefinitionParser extends AbstractBeanDefinitionParser
{

    private Class<? extends Tag> type;
    private String classpath;

    /**
     * Create new bean parser instance to process tags of given type.
     * <p>Type annotations and package-info must be compatible with JAXB
     * requirements.</p>
     * @param type type of root tag, must not be empty.
     */
    public TagBeanDefinitionParser(Class<? extends Tag> type) {
        this(type, type.getPackage().getName());
    }

    /**
     * Create new bean parser instance to process tags of given type
     * and load required classes from provided classpath.
     * <p>Type annotations and package-info must be compatible with JAXB
     * requirements.</p>
     * @param type type of root tag.
     * @param classpath colon-separated list of packages for JAXB to load.
     */
    public TagBeanDefinitionParser(Class<? extends Tag> type, String classpath) {
        if (classpath == null) {
            throw new IllegalArgumentException("Cannot process empty classpath.");
        }
        this.type = type;
        this.classpath = classpath;
    }

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        return rootBeanDefinition(TagFactoryBean.class)
                .addConstructorArgValue(element)
                .addConstructorArgValue(type)
                .addConstructorArgValue(classpath)
                .getBeanDefinition();
    }
}
