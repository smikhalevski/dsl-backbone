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

import static org.apache.commons.lang3.StringUtils.join;

@XmlTransient
public class ContainerBaseTag<
        Type extends ContainerBaseTag<Type, Parent>,
        Parent extends ContainerTag
        >
        extends BaseTag<Type, Parent>
        implements ContainerTag<Type, Parent>
{

    @SuppressWarnings("unchecked")
    private List<Tag<Type>> children = new TagChildren<Type, Tag<Type>>((Type) this);

    @Override
    @XmlTransient
    public List<Tag<Type>> getChildren() {
        return children;
    }

    /**
     * Append another child tag to container.
     * <p>If tag is already a child of container it is moved to the end of children list.</p>
     * @param tag tag to insert.
     */
    public <T extends Tag<Type>> T appendChild(T tag) {
        children.add(tag);
        return tag;
    }

    @Override
    public void configureChild(Tag<Type> tag) {
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
            out += "\nchildren = [\n\t" + join(children, ",\n").replace("\n", "\n\t") + "\n]";
        }
        return out;
    }
}
