package org.thymeleaf.context;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Wraps HTTP Servlet requests to enable {@link RequestAttributeNameChangeListener}s to be notified of
 * changes made to the request's attribute names.
 */
@WebFilter("/*")
public class ServletRequestWrappingFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletRequest requestToChain = request;
        if (request instanceof HttpServletRequest) {
            requestToChain = new AttributeNameChangeNotifyingRequestWrapper((HttpServletRequest) request);
        }
        chain.doFilter(requestToChain, response);
    }

    public void destroy() {
    }
}
