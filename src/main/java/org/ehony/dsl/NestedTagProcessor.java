/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl;

import org.ehony.dsl.annotation.Processor;
import org.ehony.dsl.api.ContainerTag;
import org.ehony.dsl.api.Tag;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NestedTagProcessor implements Processor<ContainerTag> {
    @Override
    public void process(Annotation annotation, ContainerTag target, AnnotatedElement element, ElementType elementType, Class<? super ContainerTag> type) throws Exception {
        switch (elementType) {
            case METHOD:
                target.appendChild((Tag) ((Method) element).invoke(target));
                break;
            case FIELD:
                target.appendChild((Tag) ((Field) element).get(target));
                break;
        }
    }
}
