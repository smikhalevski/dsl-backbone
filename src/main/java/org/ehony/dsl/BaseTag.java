/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl;

import org.ehony.dsl.api.*;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;
import javax.xml.namespace.QName;
import java.beans.Introspector;
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

    private String id;
    @XmlTransient
    private String name;
    private Parent parent;
    private TagContext context;
    @XmlAnyAttribute
    private Map<QName, Object> attributes = new HashMap<QName, Object>();

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
     * @return Tag name in bean identifier flavour.
     */
    public String getTagName() {
        if (name == null) {
            // If no custom tag name was defined then camel-cased class name is returned.
            return Introspector.decapitalize(getClass().getSimpleName());
        } else {
            return name;
        }
    }

    public void setCustomTagName(String name) {
        this.name = name;
    }

    /**
     * Callback strategy invoked by JAXB after unmarshalling parent element into tag object.
     * <p>By default sets parent for this tag.</p>
     * <p><font color="red">Recommended for internal use only.</font></p>
     */
    @SuppressWarnings("unchecked")
    public void afterUnmarshal(Unmarshaller target, Object tag) {
        setParent((Parent) tag);
    }

    @Override
    @XmlTransient
    public Parent getParent() {
        return parent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Parent setParent(Parent parent) {
        for (Tag tag = parent; tag != null; tag = tag.getParent()) {
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
    public Map<QName, Object> getOtherAttributes() {
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
        attributes.put(name, value);
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
     * <p>By default returns parent of this element.</p>
     *
     * @return Original parent builder instance.
     */
    public Parent end() {
        return getParent();
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
