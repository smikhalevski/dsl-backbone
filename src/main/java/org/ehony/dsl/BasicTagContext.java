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

import static org.apache.commons.lang3.Validate.*;

@XmlTransient
public class BasicTagContext implements TagContext
{

    @XmlTransient
    private Map<String, Object> beans = new HashMap<String, Object>();

    @Override
    @XmlTransient
    @SuppressWarnings("unchecked")
    public <T> T getBean(String id, Class<T> type) {
        notNull(type, "Looked up bean type expected.");
        Object bean = getBeans().get(id);
        isInstanceOf(type, bean, "Bean of " + type + " not found: " + id);
        return (T) bean;
    }

    /**
     * Get beans registered in current context.
     * @return Mapping between identifiers and bean instances.
     */
    @XmlTransient
    public Map<String, Object> getBeans() {
        return beans;
    }

    public void registerBean(String id, Object bean) {
        notBlank(id, "Nonempty identifier expected.");
        notNull(bean, "Bean expected.");
        beans.put(id, bean);
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
        registerBean(id, bean);
        return this;
    }
    
    // </editor-fold>
}
