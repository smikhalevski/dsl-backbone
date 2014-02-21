/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;

import static org.ehony.dsl.util.Validate.notBlank;

/**
 * Abstract mixin support of optional <code>id</code> attribute.
 * @param <Type> builder type returned by fluent methods.
 */
@XmlTransient
public class Identifiable<Type extends Identifiable<Type>>
{

    @XmlTransient
    private String id;

    /**
     * Get the value of the identifier property.
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
        notBlank(id, "Nonempty identifier expected.");
        this.id = id;
    }

    // <editor-fold desc="Fluent API"> 

    /**
     * Set identifier of this node.
     * <p>By default identifier is not set.</p>
     *
     * @param id nonempty identifier to assign.
     * @return Original builder instance.
     */
    @SuppressWarnings("unchecked")
    public Type id(String id) {
        setId(id);
        return (Type) this;
    }

    // </editor-fold>
}
