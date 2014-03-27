/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.spring;

import org.ehony.dsl.api.TagContext;
import org.springframework.context.ApplicationContext;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class SpringTagContext implements TagContext
{

    private ApplicationContext context;

    public SpringTagContext(ApplicationContext context) {
        this.context = context;
    }

    public ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public <T> T getBean(String id, Class<T> type) {
        return context.getBean(id, type);
    }

    @Override
    public ClassLoader getClassLoader() {
        return context.getClassLoader();
    }
}
