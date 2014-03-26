/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.extenders;

import org.ehony.dsl.ContainerBaseTag;
import org.ehony.dsl.api.ContainerTag;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;

/**
 * Base for tags that reference beans defined in context.
 * <p>Useful to build proxies for objects defined in tag context.</p>
 * 
 * @param <Type> builder type returned by fluent methods.
 * @param <Parent> type of optional parent object.
 * @param <Bean> referenced bean type.
 * @see org.ehony.dsl.api.Tag#setContext(org.ehony.dsl.api.TagContext)
 */
@XmlTransient
public class BeanReferenceBaseTag<
        Type extends BeanReferenceBaseTag<Type, Parent, Bean>,
        Parent extends ContainerTag,
        Bean
        >
        extends ContainerBaseTag<Type, Parent>
{

    private Class<? extends Bean> type;
    private String beanRef;
    private Bean bean;

    /**
     * Treat object as a bean reference.
     * @param bean object to wrap.
     */
    public BeanReferenceBaseTag(Bean bean) {
        setBean(bean);
    }

    /**
     * Creates reference for bean with implicit type resolution.
     * <p>Using bean reference without defining explicit object type may
     * cause {@link ClassCastException} on bean resolution phase.</p>
     * 
     * @param id bean identifier.
     */
    public BeanReferenceBaseTag(String id) {
        setBeanRef(id);
    }

    /**
     * Creates reference for bean of specified type with given identifier.
     * 
     * @param id bean identifier.
     * @param type type of bean to lookup.
     */
    public BeanReferenceBaseTag(String id, Class<? extends Bean> type) {
        setBeanRef(id);
        setType(type);
    }

    /**
     * Get type of exploited bean.
     */
    @XmlTransient
    public Class<? extends Bean> getType() {
        return type;
    }

    /**
     * Set type of exploited bean.
     * @param type type of bean to lookup. If <code>null</code> is provided
     *             then bean type checking would not be performed.
     */
    public final void setType(Class<? extends Bean> type) {
        this.type = type;
    }

    /**
     * Get bean identifier.
     */
    @XmlAttribute(name = "bean", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public String getBeanRef() {
        return beanRef;
    }

    /**
     * Set bean identifier.
     * <p>In case bean with given identifier is not found in execution context,
     * exception may be thrown at runtime.</p>
     * 
     * @param id nonempty bean identifier.
     */
    public final void setBeanRef(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Nonempty bean identifier expected.");
        }
        this.beanRef = id;
        this.bean = null;
    }

    /**
     * Return required bean class iff was set or {@link Object} class.
     */
    private Class<?> getRawClass() {
        if (type == null) {
            return Object.class;
        }
        return type;
    }

    /**
     * Get bean instance.
     * <p>Resolution order: if bean identifier was set search in context
     * using expected type, otherwise return preset bean instance.</p>
     */
    @XmlTransient
    @SuppressWarnings("unchecked")
    public Bean getBean() {
        if (beanRef != null) {
            setBean((Bean) getContext().getBean(beanRef, getRawClass()));
        }
        return bean;
    }

    /**
     * Set bean instance.
     */
    @SuppressWarnings("unchecked")
    public final void setBean(Bean bean) {
        Class<?> type = getRawClass();
        if (type.isInstance(bean)) {
            throw new IllegalArgumentException("Expected bean of " + type);
        }
        this.beanRef = null;
        this.bean = bean;
        this.type = (Class<Bean>) bean.getClass();
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        if (beanRef == null && bean == null) {
            throw new IllegalStateException("Bean reference or instance of " + getRawClass() + " required: " + this);
        }
    }

    // <editor-fold desc="Debug">

    @Override
    protected String getDebugInfo() {
        String info = super.getDebugInfo();
        if (beanRef != null) {
            info += "\nref = " + beanRef + "\ntype = " + getRawClass().getName();
        } else {
            info += "\nbean = " + bean;
        }
        return info;
    }

    // </editor-fold>
}