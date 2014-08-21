/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.api;

import java.beans.Introspector;

/**
 * Interface that DSL tags must implement in order to support context
 * resolution strategies and tag tree traversing mechanisms.
 *
 * @param <Parent> type of parent container tag.
 */
public interface Tag<Parent extends ContainerTag>
{

    /**
     * Get human-readable name of this tag.
     * <p>Default implementation <b>does not</b> introspect annotations and rely only on class name.</p>
     *
     * @return Tag name in bean identifier flavour.
     */
    default String getTagName() {
        return Introspector.decapitalize(getClass().getSimpleName());
    }

    /**
     * Get parent container tag.
     * @return Parent tag object.
     */
    Parent getParentTag();

    /**
     * Set parent container for this tag.
     * <p><b>Recommended implementation:</b> this tag is first detached from its current parent container
     * and then added as a child to provided parent.</p>
     *
     * @param parent container to attach this tag to as a child.
     * @return Replaced parent container, equals to <code>parent</code> argument if not changes were made.
     * @exception IllegalArgumentException cyclic dependency in tag tree.
     */
    Parent setParentTag(Parent parent);

    /**
     * Get tag context.
     * @return Context this tag is attached to.
     */
    TagContext getContext();

    /**
     * Set tag context.
     * @param context context to attach tag to.
     */
    void setContext(TagContext context);

    /**
     * Tag consistency validation strategy.
     * <p>Throws exceptions indicating that tag does not conform required rules.</p>
     */
    default void validate() throws ValidationException {
        // noop
    }

    // <editor-fold desc="Default Fluent API">

    /**
     * Element finishing strategy.
     * <p>By default returns parent of this element and tries to implicitly adopt returned type.</p>
     *
     * @return Original parent builder instance.
     */
    default Parent end() {
        return getParentTag();
    }

    /**
     * Element finishing strategy which explicitly casts parent to given type.
     * @param type type instance to cast to.
     * @return Original parent builder instance.
     */
    default <T extends Parent> T end(Class<T> type) {
        return type.cast(end());
    }

    // </editor-fold>
}
