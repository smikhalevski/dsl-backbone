/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.api;

/**
 * Interface that DSL tags must implement in order to support context
 * resolution strategies and tag tree traversing mechanisms.
 * @param <Parent> type of parent container tag.
 */
public interface Tag<Parent extends ContainerTag>
{

    /**
     * Get parent container tag.
     * @return Parent tag object.
     */
    Parent getParent();

    /**
     * Set parent container for this tag.
     * <p><b>Recommended implementation:</b> this tag is first detached from its current parent container
     * and then added as a child to provided parent.</p>
     *
     * @param parent container to attach this tag to as a child.
     * @return Replaced parent container, equals to <code>parent</code> argument if not changes were made.
     * @exception IllegalArgumentException cyclic dependency in tag tree.
     */
    Parent setParent(Parent parent);

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
    void validate() throws Exception;
}
