/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;

/**
 * Annotation processor for given object type.
 * @param <Type> type of objects to introspect annotations at.
 */
@FunctionalInterface
public interface Processor<Type> {

    /**
     * Process annotation.
     *
     * @param annotation captured annotation.
     * @param target {@linkplain AnnotationVisitor#process(Object) processed} object.
     * @param element element where annotation was captured: method, class etc.
     * @param elementType type of the element annotation is attached to.
     * @param type type that <code>target</code> object implements and where <code>element</code> is defined.
     */
    void process(Annotation annotation, Type target, AnnotatedElement element, ElementType elementType, Class<? super Type> type) throws Exception;
}
