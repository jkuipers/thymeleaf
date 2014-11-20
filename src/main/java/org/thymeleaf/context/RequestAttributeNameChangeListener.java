package org.thymeleaf.context;

/**
 * Types implementing this interface can register themselves with the {@link AttributeNameChangeNotifyingRequestWrapper}
 * in order to be notified of changes in the set of request attribute names.
 */
public interface RequestAttributeNameChangeListener {
    void attributeAdded(String name);
    void attributeRemoved(String name);
}
