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
import static java.util.stream.Collectors.*;

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
    private List<Tag<Type>> children = new TagChildren<>((Type) this);

    @Override
    @XmlTransient
    @SuppressWarnings("unchecked")
    public List<Tag<? extends Type>> getChildren() {
        return (List) children;
    }

    protected StringBuilder getDebugInfo() {
        StringBuilder out = super.getDebugInfo();
        out.append("\nchildren = [");
        if (!getChildren().isEmpty()) {
            String code = getChildren().stream().map(Tag::toString).collect(joining(",\n"));
            out.append("\n\t").append(code.replace("\n", "\n\t")).append("\n");
        }
        return out.append("]");
    }
}
