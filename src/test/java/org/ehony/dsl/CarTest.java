/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.apache.commons.io.IOUtils;
import org.ehony.dsl.annotation.AnnotationVisitor;
import org.ehony.dsl.api.ContainerTag;
import org.ehony.dsl.api.NestedTag;
import org.example.Car;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;

import static org.custommonkey.xmlunit.XMLAssert.assertEquals;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.example.Brand.Porsche;

public class CarTest
{

    private Car car;

    @Before
    public void before() throws Exception {
        car = new Car()
                .id("my-car")
                .brand(Porsche)
                .engine()
                    .id("M28.01")
                    .gears(5)
                    .attribute("http://ehony.org/", "maintenance-year", "2014")
                    .attribute("petrol", "E95")
                .end();
    }

    @Test
    public void testGetBean() throws Exception {
        car.setContext(new BasicTagContext().bean("myBean", "Test"));
        assertEquals(car.resolveContext().getBean("myBean", String.class), "Test");
        assertEquals("car", car.getTagName());
    }

    @Test
    public void testJaxbToXml() throws Exception {
        Marshaller mapper = JAXBContext.newInstance(Car.class).createMarshaller();
        mapper.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.marshal(car, out);

        String expected = IOUtils.toString(Car.class.getResourceAsStream("/car.xml"));
        assertXMLEqual(expected, out.toString());
    }

    @Test
    public void testJaxbFromXml() throws Exception {
        Unmarshaller mapper = JAXBContext.newInstance(Car.class).createUnmarshaller();
        Object observed = mapper.unmarshal(Car.class.getResourceAsStream("/car.xml"));

        AnnotationVisitor<ContainerTag> visitor = new AnnotationVisitor<>();
        visitor.bindProcessor(NestedTag.class, new NestedTagProcessor());
        visitor.process((Car) observed);

        assertEquals(car.toString(), observed.toString());
    }

    private ObjectMapper createJaxbObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Jackson JAXB support injection.
        mapper.registerModule(new JaxbAnnotationModule());
        mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    @Test
    public void testJacksonJaxbToJson() throws Exception {
        String expected = IOUtils.toString(Car.class.getResourceAsStream("/car.json"));
        String observed = createJaxbObjectMapper().writeValueAsString(car);
        JSONAssert.assertEquals(expected, observed, false);
    }

    @Test
    public void testJacksonJaxbFromJson() throws Exception {
        ObjectMapper mapper = createJaxbObjectMapper();
        Car observed = mapper.readValue(Car.class.getResourceAsStream("/car.json"), Car.class);

        AnnotationVisitor<ContainerTag> visitor = new AnnotationVisitor<>();
        visitor.bindProcessor(NestedTag.class, new NestedTagProcessor());
        visitor.process(observed);

        assertEquals(car.toString(), observed.toString());
    }
}
