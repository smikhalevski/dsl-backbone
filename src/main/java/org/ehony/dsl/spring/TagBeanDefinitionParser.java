package org.ehony.dsl.spring;

import org.springframework.beans.factory.support.*;
import org.springframework.beans.factory.xml.*;
import org.w3c.dom.*;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;

@XmlTransient
public class TagBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
{

    private Class<?> type;
    private String classpath;
    private JAXBContext jaxbContext;

    public TagBeanDefinitionParser(Class<?> type, String... paths) {
        this.type = type;
        String name = type.getName();
        classpath = name.substring(0, name.lastIndexOf('.'));
        for (String path : paths) {
            classpath += ":" + path;
        }
    }

    @Override
    protected Class getBeanClass(org.w3c.dom.Element element) {
        return type;
    }

    protected void doParse(org.w3c.dom.Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, builder);
        try {
            if (jaxbContext == null) {
                jaxbContext = JAXBContext.newInstance(classpath, getClass().getClassLoader());
            }
            Binder<Node> binder = jaxbContext.createBinder();
            Object obj = binder.unmarshal(element);
            builder.getRawBeanDefinition().setSource(obj);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
