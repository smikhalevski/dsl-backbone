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

public class TagFactoryBean
        extends AbstractFactoryBean<Tag>
        implements ApplicationContextAware
{

    private Element node;
    private Class<Tag> type;
    private String classpath;
    private ApplicationContext context;

    public void setElement(Element node) {
        this.node = node;
    }

    public void setObjectType(Class<Tag> type) {
        this.type = type;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    protected Tag createInstance() throws Exception {
        Tag tag = (Tag) JAXBContext.newInstance(classpath).createBinder().unmarshal(node);
        tag.setContext(new SpringTagContext(context));
        return tag;
    }
}
