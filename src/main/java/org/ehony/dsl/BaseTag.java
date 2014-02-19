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
import javax.xml.namespace.QName;
import java.beans.Introspector;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.Validate.*;

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
        extends Identifiable<Type>
        implements Tag<Parent>
{

    private String name;
    private Parent parent;
    private TagContext context;
    @XmlAnyAttribute
    private Map<QName, Object> attributes = new HashMap<QName, Object>();
    
    /**
     * Get human-readable name of this tag.
     * <p>If no custom tan name was defined, for <code>class UserActionTag implements Tag {&hellip;}</code>
     * string <code>userAction</code> would be returned.</p>
     * @return Tag name in bean identifier flavour. 
     */
    @XmlTransient
    public String getTagName() {
        if (isBlank(name)) {
            return Introspector.decapitalize(stripEnd(getClass().getSimpleName(), "Tag"));
        } else {
            return name;
        }
    }

    public void setTagName(String name) {
        notBlank(name, "Nonempty tag name expected.");
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
            isTrue(tag != this, "Detected cyclic dependency.");
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
     */
    @Override
    public String toString() {
        return toString(getDebugInfo());
    }

    /**
     * Line feed separated string of tag-describing parameters.
     * <p>Result of this method must not include tag name.</p>
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
     * @param info parameters to display for this tag.
     */
    protected String toString(String info) {
        String out = getTagName();
        if (hasIdentifier()) {
            out += "#" + getId();
        } else {
            out += "@" + Integer.toHexString(hashCode());
        }
        out += '{';
        if (isNotBlank(info)) {
            info = strip(info, "\n");
            if (info.contains("\n")) {
                // Indenting info iff contains line feeds.
                out += "\n\t" + info.replace("\n", "\n\t") + "\n";
            } else {
                out += info;
            }
        }
        return out + '}';
    }

    // </editor-fold>
}
