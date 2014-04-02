/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl;

import org.ehony.dsl.api.Tag;
import org.example.Car;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static junit.framework.Assert.*;

public class SpringIntegrationTest
{

    @Test
    public void testTagLookup() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/test-context.xml");
        Car car = context.getBean("my-car", Car.class);

        assertNotNull("Could not retrieve car from context.", car);

        Tag engine = car.getChildren().get(0);
        assertEquals("Spring proxy failed to find bean.", engine.getContext().getBean("my-car", Car.class), car);
        assertNotNull("Failed to build parent-child relation.", engine.getParentTag());
        assertEquals(engine.toString(), "engine#M28.01{}");
    }
}
