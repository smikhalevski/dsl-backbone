/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl;

import org.ehony.dsl.api.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;
import javax.xml.namespace.QName;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Tag is a mixin with optional identifier assignment and arbitrary attributes.
 * <p>This class <b>must be</b> annotated {@link XmlTransient} to allow use of
 * {@link XmlValue} annotation on members of subclasses of {@link BaseTag}.</p>
 *
 * @param <Type> builder type returned by fluent methods.
 * @param <Parent> type of optional parent object.
 */
@XmlTransient
public class BaseTag<
        Type extends BaseTag<Type, Parent>,
        Parent extends ContainerTag
        >
        implements Tag<Parent>
{

    @XmlTransient
    private String id;
    @XmlTransient
    private String name;
    @XmlTransient
    private Parent parent;
    @XmlTransient
    private TagContext context;
    @XmlTransient
    private Map<QName, Object> attributes;

    /**
     * Get the value of the optional identifier property.
     */
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    public String getId() {
        return id;
    }

    /**
     * Set the value of the identifier property.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get human-readable name of this tag.
     * <p>Returned name <b>does not</b> introspect JAXB annotations and rely only on class name and custom name.
     * If no custom tag name was defined then camel-cased class name is returned.</p>
     * 
     * @return Tag name in bean identifier flavour.
     * @see #setCustomTagName(String)
     */
    @XmlTransient
    public String getTagName() {
        if (name == null) {
            return getClassTagName(getClass());
        } else {
            return name;
        }
    }

    /**
     * Extracts name of the tag from JAXB annotations specified for given class.
     * @param type class to introspect.
     * @return Effective XML name of the given class.
     */
    public static String getClassTagName(Class<?> type) {
        String name = Introspector.decapitalize(type.getSimpleName());
        for (Annotation a : type.getAnnotations()) {
            if (a instanceof XmlRootElement) {
                name = ((XmlRootElement) a).name();
            }
            if (a instanceof XmlElement) {
                name = ((XmlElement) a).name();
            }
            if (a instanceof XmlType) {
                name = ((XmlType) a).name();
            }
        }
        return name;
    }

    public void setCustomTagName(String name) {
        this.name = name;
    }

    @Override
    @XmlTransient
    public Parent getParentTag() {
        return parent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Parent setParentTag(Parent parent) {
        for (Tag tag = parent; tag != null; tag = tag.getParentTag()) {
            if (this == tag) {
                throw new IllegalArgumentException("Detected cyclic dependency.");
            }
        }
        Parent tag = this.parent;
        if (parent != tag) {
            if (tag != null) {
                this.parent = null;
                tag.getChildren().remove(this);
            }
            if (parent != null) {
                this.parent = parent;
                parent.getChildren().add(this);
            }
        }
        return tag;
    }

    /**
     * Strategy to resolve context from tag hierarchy.
     * <p>By default, if context is not defined then parent context is returned.</p>
     * @return {@link TagContext} or <code>null</code> if context was not set for tag or any of its ancestors.
     */
    @Override
    @XmlTransient
    public TagContext getContext() {
        if (context != null) {
            return context;
        }
        if (parent != null) {
            return parent.getContext();
        }
        return null;
    }

    @Override
    public void setContext(TagContext context) {
        this.context = context;
    }

    /**
     * Get attributes which were not explicitly defined by schema.
     */
    @XmlAnyAttribute
    public Map<QName, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<QName, Object>();
        }
        return attributes;
    }

    public void validate() throws Exception {
        // noop
    }

    // <editor-fold desc="Fluent API">

    /**
     * Set identifier of this node.
     * <p>By default identifier is not set.</p>
     *
     * @param id identifier to assign.
     * @return Original builder instance.
     */
    @SuppressWarnings("unchecked")
    public Type id(String id) {
        setId(id);
        return (Type) this;
    }

    /**
     * Add an optional attribute with arbitrary qualified name.
     *
     * @param name name of the attribute.
     * @param value value to assign to added attribute.
     * @return Original builder instance.
     */
    @SuppressWarnings("unchecked")
    public Type attribute(QName name, Object value) {
        getAttributes().put(name, value);
        return (Type) this;
    }

    /**
     * Add an optional attribute with arbitrary namespace and name.
     *
     * @param namespace required namespace.
     * @param name name of the attribute.
     * @param value value to assign to added attribute.
     * @return Original builder instance.
     */
    public Type attribute(String namespace, String name, Object value) {
        return attribute(new QName(namespace, name), value);
    }

    /**
     * Add an optional attribute in local namespace.
     *
     * @param name local name of the attribute.
     * @param value value to assign to added attribute.
     * @return Original builder instance.
     */
    public Type attribute(String name, Object value) {
        return attribute(new QName(name), value);
    }

    /**
     * Element finishing strategy.
     * <p>By default returns parent of this element and tries to implicitly adopt returned type.</p>
     *
     * @return Original parent builder instance.
     */
    @SuppressWarnings("unchecked")
    public <T extends Parent> T end() {
        return (T) getParentTag();
    }

    /**
     * Element finishing strategy which explicitly casts parent to given type.
     * @param type type instance to cast to.
     * @return Original parent builder instance.
     */
    @SuppressWarnings("unchecked")
    public <T extends Parent> T end(Class<T> type) {
        return (T) end();
    }

    // </editor-fold>

    // <editor-fold desc="Debug">

    /**
     * Output debug info about structure of this tag.
     * {@inheritDoc}
     * @see #getDebugInfo()
     */
    @Override
    public String toString() {
        return toString(getDebugInfo());
    }

    /**
     * String of tag-describing parameters.
     * <p><b>Recommended implementation:</b> description of each parameter
     * should start with a line feed character: <code><b>\n</b>key=value</code>.</p>
     * @return String of parameters.
     */
    @XmlTransient
    protected String getDebugInfo() {
        if (context != null) {
            return "\ncontext = " + context;
        } else {
            return "";
        }
    }

    /**
     * Returns string with formatted debug information about this tag and its children.
     * @param info line feed separated parameters to display for this tag.
     * @see #getDebugInfo()
     */
    protected String toString(String info) {
        StringBuilder out = new StringBuilder(getTagName());
        String id = getId();
        if (id != null) {
            out.append('#').append(id);
        } else {
            out.append('@').append(Integer.toHexString(hashCode()));
        }
        out.append('{');
        if (info != null) {
            if (info.contains("\n")) {
                // Indenting info only when it contains line feeds.
                out.append(info.replace("\n", "\n\t")).append('\n');
            } else {
                out.append(info);
            }
        }
        return out.append('}').toString();
    }

    // </editor-fold>
}
