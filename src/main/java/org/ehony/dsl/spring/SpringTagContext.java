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

/**
 * Proxy to Spring context.
 */
@XmlTransient
public class SpringTagContext implements TagContext
{

    private ApplicationContext context;

    public SpringTagContext(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Spring context that holds configuration of an application.
     * <p>Originally assigned to root tag created in returned context.</p>
     */
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
