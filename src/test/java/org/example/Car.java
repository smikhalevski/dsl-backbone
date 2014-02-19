/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.example;

import org.ehony.dsl.ContainerBaseTag;
import org.ehony.dsl.api.ContainerTag;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class Car extends ContainerBaseTag<Car, ContainerTag>
{

    @XmlAttribute
    public Brand brand;
    public Engine engine;
    
    public Car brand(Brand brand) {
        this.brand = brand;
        return this;
    }

    public Engine engine() {
        if (engine == null) {
            // Car must have an engine.
            engine = appendChild(new Engine());
        }
        return engine;
    }
}
