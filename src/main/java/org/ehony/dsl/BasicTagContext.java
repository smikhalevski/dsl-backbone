/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl;

import org.ehony.dsl.api.TagContext;

import javax.xml.bind.annotation.XmlTransient;
import java.util.*;

/**
 * {@link HashMap}-backed {@link TagContext}.
 */
@XmlTransient
public class BasicTagContext implements TagContext
{

    @XmlTransient
    private Map<String, Object> beans = new HashMap<String, Object>();

    @Override
    @XmlTransient
    @SuppressWarnings("unchecked")
    public <T> T getBean(String id, Class<T> type) {
        if (beans.containsKey(id)) {
            Object bean = beans.get(id);
            if (type != null && !type.isInstance(bean)) {
                throw new IllegalArgumentException("Bean of " + type + " not found: " + id);
            }
            return (T) bean;
        }
        throw new IllegalArgumentException("Bean not found: " + id);
    }

    @Override
    @XmlTransient
    public ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }

    /**
     * Get beans registered in current context.
     * @return Mapping between identifiers and bean instances.
     */
    @XmlTransient
    public Map<String, Object> getBeans() {
        return beans;
    }

    // <editor-fold desc="Fluent API">

    /**
     * Registers new bean in current context.
     *
     * @param id nonempty identifier.
     * @param bean bean instance.
     * @return Original {@link BasicTagContext} instance.
     */
    public BasicTagContext bean(String id, Object bean) {
        beans.put(id, bean);
        return this;
    }
    
    // </editor-fold>
}
