/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl;

import org.ehony.dsl.api.*;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@XmlTransient
public class ContainerBaseTag<
        Type extends ContainerBaseTag<Type, Parent>,
        Parent extends ContainerTag
        >
        extends BaseTag<Type, Parent>
        implements ContainerTag<Type, Parent>
{

    @XmlTransient
    @SuppressWarnings("unchecked")
    private List<Tag<Type>> children = new TagChildren<Type, Tag<Type>>((Type) this);

    @Override
    @XmlTransient
    @SuppressWarnings("unchecked")
    public List<Tag<? extends Type>> getChildren() {
        return (List) children;
    }

    /**
     * Append another child tag to container.
     * <p>If tag is already a child of container it is moved to the end of children list.</p>
     * @param tag tag to insert.
     */
    @SuppressWarnings("unchecked")
    public <T extends Tag<? extends Type>> T appendChild(T tag) {
        children.add((Tag<Type>) tag); // The same as tag.setParentTag(this)
        return tag;
    }

    @Override
    public void configureChild(Tag<? extends Type> tag) {
        // noop
    }

    /**
     * {@inheritDoc}
     * <p>Validation of child tags must also be performed in this method.</p>
     */
    @Override
    public void validate() throws Exception {
        super.validate();
        for (Tag<Type> tag : children) {
            tag.validate();
        }
    }

    @Override
    protected String getDebugInfo() {
        String out = super.getDebugInfo();
        if (!children.isEmpty()) {
            StringBuilder code = new StringBuilder();
            for (Tag<Type> tag : children) {
                code.append(",\n").append(tag);
            }
            out += "\nchildren = [\n\t" + code.substring(2).replace("\n", "\n\t") + "\n]";
        }
        return out;
    }
}
