/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl.api;

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
}
