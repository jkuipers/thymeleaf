package org.thymeleaf.context;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Used to notify subscribed listeners about changes in the servlet request's set of attribute names.
 * Obtaining this set is an expensive operation, so classes may opt to cache the set. This wrapper
 * allows those classes to keep their cached set up to date when custom Thymeleaf processors or e.g.
 * JSP pages add new request attributes directly to the request.
 */
public class AttributeNameChangeNotifyingRequestWrapper extends HttpServletRequestWrapper {

    private Set<String> names = new HashSet<String>();
    private List<RequestAttributeNameChangeListener> nameChangeListeners = new CopyOnWriteArrayList<RequestAttributeNameChangeListener>();

    /**
     * Tries to find if the given request is itself an AttributeNameChangeNotifyingRequestWrapper or if it is
     * a wrapper where one of the wrapped requests is an AttributeNameChangeNotifyingRequestWrapper and if so,
     * returns it.
     * @param request
     * @return the wrapper, or {@code null} if no wrapper could be detected for the given request
     */
    public static AttributeNameChangeNotifyingRequestWrapper findWrapper(ServletRequest request) {
        if (request instanceof AttributeNameChangeNotifyingRequestWrapper) {
            return (AttributeNameChangeNotifyingRequestWrapper) request;
        }
        if (request instanceof ServletRequestWrapper) {
            return findWrapper(((ServletRequestWrapper) request).getRequest());
        }
        return null;
    }

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public AttributeNameChangeNotifyingRequestWrapper(HttpServletRequest request) {
        super(request);
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            names.add(attributeNames.nextElement());
        }
    }

    public void subscribe(RequestAttributeNameChangeListener nameChangeListener) {
        nameChangeListeners.add(nameChangeListener);
    }

    @Override
    public void setAttribute(String name, Object o) {
        super.setAttribute(name, o);
        if (names.add(name)) {
            for (RequestAttributeNameChangeListener listener : nameChangeListeners) {
                listener.attributeAdded(name);
            }
        }
    }

    @Override
    public void removeAttribute(String name) {
        super.removeAttribute(name);
        if (names.remove(name)) {
            for (RequestAttributeNameChangeListener listener : nameChangeListeners) {
                listener.attributeRemoved(name);
            }
        }
    }
}
