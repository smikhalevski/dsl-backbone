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
import java.util.function.Predicate;

/**
 * JAXB-annotated implementation of forward tolerant identifiable DSL tag.
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
        implements Tag<Parent>,
                   Identifiable<Type>,
                   Tolerant<Type>
{

    @XmlTransient
    private String id;
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
                throw new IllegalArgumentException("Cyclic tag reference.");
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
        return context;
    }

    @Override
    public void setContext(TagContext context) {
        this.context = context;
    }

    public TagContext resolveContext() {
        for (Tag tag = this; tag != null; tag = tag.getParentTag()) {
            if (tag.getContext() != null) {
                return tag.getContext();
            }
        }
        return null;
    }

    @Override
    @XmlAnyAttribute
    public Map<QName, Object> getCustomAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        return attributes;
    }

    // <editor-fold desc="Debug">

    /**
     * Output debug info about structure of this tag.
     * {@inheritDoc}
     * @see #getDebugInfo()
     */
    @Override
    public String toString() {
        return toDebugString(getDebugInfo().toString());
    }

    /**
     * {@link StringBuilder} containing tag-describing parameters.
     * <p><b>Recommended implementation:</b> description of each parameter
     * should start with a line feed character: <code><b>\n</b>key=value</code>.</p>
     */
    protected StringBuilder getDebugInfo() {
        return new StringBuilder().append("\ncontext = ").append(getContext());
    }

    /**
     * Returns string with formatted debug information about this tag and its children.
     * @param info line feed separated parameters to display for this tag.
     * @see #getDebugInfo()
     */
    protected String toDebugString(String info) {
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
