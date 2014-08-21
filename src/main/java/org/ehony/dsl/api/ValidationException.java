/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.api;

/**
 * DSL tag validation exception.
 */
public class ValidationException extends Exception
{

    public ValidationException(String message) {
        super(message);
    }
}
