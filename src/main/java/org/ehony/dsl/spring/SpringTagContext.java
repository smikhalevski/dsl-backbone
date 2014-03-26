package org.ehony.dsl.spring;

import org.ehony.dsl.api.*;
import org.springframework.context.*;

import javax.xml.bind.annotation.*;

/**
 * 
 */
@XmlTransient
public class SpringTagContext implements TagContext, ApplicationContextAware
{

    private ApplicationContext context;
    
    @Override
    public void setApplicationContext(ApplicationContext context) {
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
