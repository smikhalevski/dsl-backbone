/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.example;

import org.ehony.dsl.BaseTag;
import org.ehony.dsl.api.ValidationException;

import javax.xml.bind.annotation.XmlAttribute;

public class Engine extends BaseTag<Engine, Car>
{

    @XmlAttribute
    public Integer gears;

    public Engine gears(int gears) {
        this.gears = gears;
        return this;
    }

    @Override
    public void validate() throws ValidationException {
        if (gears == null) {
            throw new ValidationException("Gears expected.");
        }
    }
}
