/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl;

import org.example.*;
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

        Engine engine = (Engine) car.getChildren().get(0);
        assertEquals("Spring proxy failed to find bean.", engine.resolveContext().getBean("my-car", Car.class), car);
        assertNotNull("Failed to build parent-child relation.", engine.getParentTag());
        assertEquals(engine.toString(), "engine#M28.01{\n\tcontext = null\n}");
    }
}
