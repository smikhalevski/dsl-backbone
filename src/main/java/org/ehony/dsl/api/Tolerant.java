package org.ehony.dsl.api;

import javax.xml.namespace.QName;
import java.util.Map;

/**
 * Interface to support forward compatibility of DSL tags.
 * @param <Type> builder type returned by fluent methods.
 */
public interface Tolerant<Type extends Tolerant<Type>>
{

    /**
     * Get attributes which were not explicitly defined by schema.
     */
    Map<QName, Object> getCustomAttributes();

    // <editor-fold desc="Default Fluent API">

    /**
     * Add an optional attribute with arbitrary qualified name.
     *
     * @param name name of the attribute.
     * @param value value to assign to added attribute.
     * @return Original builder instance.
     */
    @SuppressWarnings("unchecked")
    default Type attribute(QName name, Object value) {
        getCustomAttributes().put(name, value);
        return (Type) this;
    }

    /**
     * Add an optional attribute with arbitrary namespace and name.
     *
     * @param namespace required namespace.
     * @param name name of the attribute.
     * @param value value to assign to added attribute.
     * @return Original builder instance.
     */
    default Type attribute(String namespace, String name, Object value) {
        return attribute(new QName(namespace, name), value);
    }

    /**
     * Add an optional attribute in local namespace.
     *
     * @param name local name of the attribute.
     * @param value value to assign to added attribute.
     * @return Original builder instance.
     */
    default Type attribute(String name, Object value) {
        return attribute(new QName(name), value);
    }

    // </editor-fold>
}
