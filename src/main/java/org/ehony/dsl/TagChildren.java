/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl;

import org.ehony.dsl.api.*;

import javax.xml.bind.annotation.XmlTransient;
import java.util.*;

/**
 * List of tags which ensure that all its items have the same parent.
 * <p>Parent is set to <code>null</code> when item is removed from the list.</p>
 * <p>List ensures that all of its get, set, add and remove operations
 * are done via corresponding basic methods, ex. {@link List#add(Object)} invokes
 * {@link List#add(int, Object)} under the hood.</p>
 * <p>List throws exceptions when <code>null</code> values are inserted.</p>
 */
@XmlTransient
public class TagChildren<
        Parent extends ContainerTag<Parent, ?>,
        Child extends Tag<Parent>
        >
        extends AbstractList<Child>
{

    private Parent parent;
    private List<Child> list = new ArrayList<Child>();

    /**
     * Creates new list of container children.
     * @param parent parent container for all tags in created list.
     */
    public TagChildren(Parent parent) {
        this.parent = parent;
    }

    /**
     * Get container tag which holds these children.
     * @return Parent tag object.
     */
    public Parent getParent() {
        return parent;
    }

    @Override
    public Child get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    /**
     * Set tag as a child at given position for this container.
     * <p>When given tag is already a child of this container it is moved
     * to provided index, if needed.</p>
     * {@inheritDoc}
     * 
     * @param offset index of the tag to replace.
     * @param tag tag to be stored at the specified position.
     * @return Tag previously at the specified position.
     */
    @Override
    public Child set(int offset, Child tag) {
        Child before = get(offset);
        boolean noop = tag == before || (tag != null && tag.equals(before));
        if (!noop) {
            // First add new tag to avoid excessive reconfiguration,
            // then remove following element because it was shifted.
            add(offset, tag);
            remove(offset + 1);
        }
        return before;
    }

    /**
     * Insert provided tag as a child with given offset to this container.
     * <p>This operation detaches tag from its previous parent. When given
     * tag is already a child of this container it is moved to provided
     * offset, if needed.</p>
     * {@inheritDoc}
     * 
     * @param offset index at which the specified tag is to be inserted.
     * @param tag tag to be inserted.
     */
    @Override
    public void add(int offset, Child tag) {
        int size = list.size();
        if (offset > size  || offset < 0) {
            throw new IndexOutOfBoundsException("Index " + offset + " out of range [0, " + size + "]");
        }
        int index = list.indexOf(tag);
        if (index != offset) {
            if (index >= 0) {
                if (index < offset) {
                    offset--;
                }
                list.remove(index);
            }
            list.add(offset, tag);
            if (index < 0 && tag != null) {
                // Omit tag configuration if only rearrangement required.
                parent.configureChild(tag);
                tag.setParentTag(parent);
            }
        }
    }

    /**
     * {@inheritDoc}
     * Detaches child with given index from container.
     * @return Element previously at the specified position.
     */
    @Override
    public Child remove(int index) {
        Child tag = list.remove(index);
        if (tag != null) {
            tag.setParentTag(null);
        }
        return tag;
    }
}
