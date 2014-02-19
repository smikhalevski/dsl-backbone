/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl;

import org.apache.commons.io.IOUtils;
import org.example.Car;
import org.junit.*;

import javax.xml.bind.*;
import java.io.*;

import static org.custommonkey.xmlunit.XMLAssert.*;
import static org.example.Brand.Porsche;

public class CarTest
{

    private Car car;
    private InputStream in;

    @Before
    public void before() throws Exception {
        car = new Car()
                .id("my-car")
                .brand(Porsche)
                .engine()
                    .id("M28.01")
                    .gears(5)
                    .end();
        in = Car.class.getResourceAsStream("/car.xml");
    }

    @Test
    public void testGetBean() {
        BasicTagContext context = new BasicTagContext();
        context.registerBean("myBean", "Test");
        car.setContext(context);
        assertEquals(car.getContext().getBean("myBean", String.class), "Test");
    }

    @Test
    public void testToXml() throws Exception {
        Marshaller m = JAXBContext.newInstance(Car.class).createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.marshal(car, out);

        String xml = IOUtils.toString(in);
        assertXMLEqual(xml, out.toString());
    }

    @Test
    public void testFromXml() throws Exception {
        Unmarshaller m = JAXBContext.newInstance(Car.class).createUnmarshaller();
        assertEquals(car.toString(), m.unmarshal(in).toString());
    }
}
