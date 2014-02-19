/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.api;

import java.util.List;

/**
 * Interface that DSL tags must implement in order to support addition, removal and replacement of child tags.
 */
public interface ContainerTag<
        Type extends ContainerTag<Type, Parent>,
        Parent extends ContainerTag
        >
        extends Tag<Parent>
{

    /**
     * Get list of child tags stored in this container.
     * <p><b>Recommended implementation:</b> remove, add and set actions of returned list are seamlessly propagated
     * to container structure in order to preserve consistency of parent-child relations.</p>
     * @return List of container children.
     */
    List<Tag<Type>> getChildren();

    /**
     * Custom configuration strategy for children of this tag.
     * <p><b>Recommended implementation:</b> executed when child tag is first added to this container.</p>
     * @param tag tag to configure.
     */
    void configureChild(Tag<Type> tag);
}
