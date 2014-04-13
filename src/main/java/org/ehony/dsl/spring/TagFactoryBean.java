/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.spring;

import org.ehony.dsl.api.Tag;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.*;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;

/**
 * Bean that constructs {@link Tag} instance from provided XML element.
 */
public class TagFactoryBean
        extends AbstractFactoryBean<Tag>
        implements ApplicationContextAware
{

    private Element node;
    private Class<Tag> type;
    private String classpath;
    private ApplicationContext context;
    private Tag tag;

    /**
     * Create new factory bean that can construct bean of given type
     * via parsing node with JAXB configured with provided classpath.
     * @param node element to parse.
     * @param type expected object type, can be empty.
     * @param classpath classpath for JAXB to load.
     */
    public TagFactoryBean(Element node, Class<Tag> type, String classpath) {
        this.node = node;
        this.type = type;
        this.classpath = classpath;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Class<Tag> getObjectType() {
        return type;
    }

    @Override
    protected Tag createInstance() throws Exception {
        if (tag == null) {
            tag = (Tag) JAXBContext.newInstance(classpath).createBinder().unmarshal(node);
            tag.setContext(new SpringTagContext(context));
        }
        return tag;
    }
}
