/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.api;

import java.util.List;

/**
 * Interface that DSL tags must implement in order to support addition,
 * removal and replacement of child tags.
 */
public interface ContainerTag<
        Type extends ContainerTag<Type, Parent>,
        Parent extends ContainerTag
        >
        extends Tag<Parent>
{

    /**
     * Get list of child tags stored in this container.
     * <p>Remove, add and set actions of returned list must be seamlessly propagated to container
     * structure in order to preserve consistency of parent-child relations.</p>
     *
     * @return List of container children.
     */
    List<Tag<? extends Type>> getChildren();

    /**
     * Custom configuration strategy for children of this tag.
     * <p>Expected to be executed once when child tag is <b>first time</b> added to this container.</p>
     *
     * @param tag tag to configure.
     */
    default void configureChild(Tag<? extends Type> tag) {
        // noop
    }

    /**
     * {@inheritDoc}
     * <p>Children are recursively validated in this method.</p>
     */
    default void validate() throws ValidationException {
        for (Tag<? extends Type> tag : getChildren()) {
            tag.validate();
        }
    }

    // <editor-fold desc="Default Fluent API">

    /**
     * Append another child tag to container.
     * <p>If tag is already a child of container it should moved to the end of children list.</p>
     * <p>This should do the same as <code>tag.setParentTag(this)</code> to make operation reversible.</p>
     *
     * @param tag tag to insert.
     */
    default <T extends Tag<? extends Type>> T appendChild(T tag) {
        getChildren().add(tag);
        return tag;
    }

    // </editor-fold>
}
