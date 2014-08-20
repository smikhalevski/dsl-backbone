package org.ehony.dsl;

import org.ehony.dsl.api.*;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

/**
 * Callback strategy invoked by JAXB after unmarshalling parent element into tag object.
 */
public class TagParentListener extends Unmarshaller.Listener {

    private <T> T castToObject(Object from, Class<T> type) {
        if (from == null) {
            return null;
        }
        if (type.isInstance(from)) {
            return type.cast(from);
        }
        if (from instanceof JAXBElement) {
            return castToObject(((JAXBElement) from).getValue(), type);
        }
        throw new ClassCastException("Cannot cast " + from + " to " + type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterUnmarshal(Object tag, Object parent) {
        castToObject(tag, Tag.class).setParentTag(castToObject(parent, ContainerTag.class));
    }
}
