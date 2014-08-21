/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.api;

/**
 * Interface to support optional <code>id</code> attribute.
 * @param <Type> builder type returned by fluent methods.
 */
public interface Identifiable<Type extends Identifiable<Type>>
{

    /**
     * Get the value of the identifier property.
     */
    String getId();

    /**
     * Set the value of the identifier property.
     */
    void setId(String id);

    // <editor-fold desc="Default Fluent API">

    /**
     * Set identifier of this node.
     * <p>By default identifier is not set.</p>
     *
     * @param id nonempty identifier to assign.
     * @return Original builder instance.
     */
    @SuppressWarnings("unchecked")
    default Type id(String id) {
        setId(id);
        return (Type) this;
    }

    // </editor-fold>
}