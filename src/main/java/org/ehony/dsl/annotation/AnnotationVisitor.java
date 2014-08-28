/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import static java.util.Arrays.*;
import static java.util.function.Predicate.*;

public class AnnotationVisitor<Type> {

    private Map<Class<?>, Processor<Type>> processors = new HashMap<>();
    private Set<ElementType> targets = new HashSet<>();

    public void bindProcessor(Class<? extends Annotation> type, Processor<Type> processor) {
        Target target = type.getAnnotation(Target.class);
        if (target != null) {
            stream(target.value()).forEach(this.targets::add);
        } else {
            stream(values()).filter(isEqual(TYPE_PARAMETER).negate()).forEach(this.targets::add);
        }
        processors.put(type, processor);
    }

    private void processAnnotations(Type target, AnnotatedElement element, ElementType elementType, Class<?> type) {
        for (Annotation annotation : element.getAnnotations()) {
            Processor annotationListener = processors.get(annotation.annotationType());
            if (annotationListener != null) {
                try {
                    annotationListener.process(annotation, target, element, elementType, type);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void processType(Type target, Class<?> type) {
        if (type != null) {
            Arrays.stream(type.getInterfaces()).forEach(c -> processType(target, c));
            processType(target, type.getSuperclass());

            Arrays.stream(type.getDeclaredFields()).forEach(f -> processAnnotations(target, f, FIELD, type));
            Arrays.stream(type.getDeclaredMethods()).forEach(m -> processAnnotations(target, m, METHOD, type));
            processAnnotations(target, type, TYPE, type);
        }
    }

    public void process(Type target) {
        processType(target, target.getClass());
    }
}
