package org.ehony.dsl;

import org.ehony.dsl.api.*;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

/**
 * Callback strategy invoked by JAXB after unmarshalling parent element into tag object.
 */
public class TagParentListener extends Unmarshaller.Listener {

    private <T extends Tag> T toTag(Object tag, Class<T> type) {
        if (tag instanceof JAXBElement) {
            tag = ((JAXBElement) tag).getValue();
        }
        return type.cast(tag);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterUnmarshal(Object tag, Object parent) {
        if (parent != null) {
            toTag(tag, Tag.class).setParentTag(toTag(parent, ContainerTag.class));
        }
    }
}
