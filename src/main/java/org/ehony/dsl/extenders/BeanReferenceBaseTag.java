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

import static org.ehony.dsl.util.Validate.*;

/**
 * Base for tags that reference beans defined in context.
 * <p>Useful to build proxies for objects defined in execution context.</p>
 * 
 * @param <Type> builder type returned by fluent methods.
 * @param <Parent> type of optional parent object.
 * @param <Bean> referenced bean type.
 */
@XmlTransient
public class BeanReferenceBaseTag<
        Type extends BeanReferenceBaseTag<Type, Parent, Bean>,
        Parent extends ContainerTag,
        Bean
        >
        extends ContainerBaseTag<Type, Parent>
{

    private Class<Bean> type;
    private String beanRef;
    private Bean bean;

    /**
     * Create reference to bean of given type.
     * <p>Recommended for internal use only.</p>
     * @param type type of bean.
     */
    protected BeanReferenceBaseTag(Class<Bean> type) {
        notNull(type, "Bean type expected.");
        setType(type);
    }

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
    public BeanReferenceBaseTag(String id, Class<Bean> type) {
        setBeanRef(id);
        setType(type);
    }

    /**
     * Get type of exploited bean.
     */
    @XmlTransient
    public Class<Bean> getType() {
        return type;
    }

    /**
     * Set type of exploited bean.
     * @param type type of bean to lookup. If <code>null</code> is provided
     *             then bean type checking would not be performed.
     */
    public final void setType(Class<Bean> type) {
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
        notBlank(id, "Nonempty bean identifier expected.");
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
        isInstanceOf(type, bean, "Expected bean of " + type);
        this.beanRef = null;
        this.bean = bean;
        this.type = (Class<Bean>) bean.getClass();
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        validState(beanRef != null || bean != null, "Bean reference or instance of " + getRawClass() + " required: " + this);
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

    // <editor-fold>
}