/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.spring;

import org.springframework.beans.factory.support.*;
import org.springframework.beans.factory.xml.*;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class TagBeanDefinitionParser extends AbstractBeanDefinitionParser
{

    private Class<?> type;
    private String classpath;

    public TagBeanDefinitionParser(Class<?> type) {
        this(type, "");
    }

    public TagBeanDefinitionParser(Class<?> type, String classpath) {
        this.type = type;
        this.classpath = type.getPackage().getName() + ":" + classpath;
    }

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(TagFactoryBean.class);
        factory.addPropertyValue("element", element);
        factory.addPropertyValue("objectType", type);
        factory.addPropertyValue("classpath", classpath);
        return factory.getBeanDefinition();
    }
}
