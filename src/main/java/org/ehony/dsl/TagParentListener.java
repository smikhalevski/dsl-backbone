package org.ehony.dsl;

import org.ehony.dsl.api.*;

import javax.xml.bind.Unmarshaller;

/**
 * Callback strategy invoked by JAXB after unmarshalling parent element into tag object.
 */
public class TagParentListener extends Unmarshaller.Listener {

    @Override
    @SuppressWarnings("unchecked")
    public void afterUnmarshal(Object tag, Object parent) {
        if (tag instanceof Tag) {
            ((Tag) tag).setParentTag((ContainerTag) parent);
        }
    }
}
