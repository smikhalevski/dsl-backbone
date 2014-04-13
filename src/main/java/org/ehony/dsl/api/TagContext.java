/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.api;

/**
 * Context of DSL tag.
 */
public interface TagContext
{

    /**
     * Get bean defined in this context.
     * 
     * @param id nonempty bean identifier.
     * @param type type of looked up bean.
     * @return Bean instance of requested type.
     */
    <T> T getBean(String id, Class<T> type);

    /**
     * Get class loader for this context.
     */
    ClassLoader getClassLoader();
}
