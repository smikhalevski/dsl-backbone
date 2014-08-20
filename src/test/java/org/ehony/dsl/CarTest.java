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
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.apache.commons.io.IOUtils;
import org.example.Car;
import org.junit.*;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.xml.bind.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import static org.custommonkey.xmlunit.XMLAssert.*;
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
    public void testGetBean() {
        car.setContext(new BasicTagContext().bean("myBean", "Test"));
        assertEquals(car.getContext().getBean("myBean", String.class), "Test");
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
    public void testJaxbFromXmlByObject() throws Exception {
        Unmarshaller mapper = JAXBContext.newInstance(Car.class).createUnmarshaller();
        mapper.setListener(new TagParentListener());

        Object observed = mapper.unmarshal(Car.class.getResourceAsStream("/car.xml"));
        assertEquals(car.toString(), observed.toString());
    }

    @Test
    public void testJaxbFromXmlBySource() throws Exception {
        Unmarshaller mapper = JAXBContext.newInstance(Car.class).createUnmarshaller();
        mapper.setListener(new TagParentListener());

        Source source = new StreamSource(Car.class.getResourceAsStream("/car.xml"));
        Car observed = mapper.unmarshal(source, Car.class).getValue();
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
    @Ignore("Jackson does not support callbacks on after mapping")
    public void testJacksonJaxbFromJson() throws Exception {
        ObjectMapper mapper = createJaxbObjectMapper();

        Car observed = mapper.readValue(Car.class.getResourceAsStream("/car.json"), Car.class);
        // TODO How to implement parent reference for Tag using Jackson?
        assertEquals(car.toString(), observed.toString());
    }

    private ObjectMapper createJsr353ObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JSR353Module());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    @Test
    public void testJacksonJsr353ToJson() throws Exception {
        String expected = IOUtils.toString(Car.class.getResourceAsStream("/car.json"));
        String observed = createJsr353ObjectMapper().writeValueAsString(car);
        JSONAssert.assertEquals(expected, observed, false);
    }
}
