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
     */
    @Override
    public Child set(int index, Child tag) {
        Child oldTag = get(index);
        if (!tag.equals(oldTag)) {
            remove(index);
            add(index, tag);
        }
        return oldTag;
    }

    /**
     * Insert provided tag as a child with given offset to this container.
     * <p>This operation detaches tag from its previous parent. When given
     * tag is already a child of this container it is moved to provided
     * offset, if needed.</p>
     * {@inheritDoc}
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
            if (index < 0) {
                // Omit tag configuration if only rearrangement required.
                parent.configureChild(tag);
                tag.setParent(parent);
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
            tag.setParent(null);
        }
        return tag;
    }
}
