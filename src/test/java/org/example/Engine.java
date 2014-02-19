/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.example;

import org.ehony.dsl.BaseTag;

import javax.xml.bind.annotation.XmlAttribute;

public class Engine extends BaseTag<Engine, Car>
{

    @XmlAttribute
    public Integer gears;

    public Engine gears(int gears) {
        this.gears = gears;
        return this;
    }
}
