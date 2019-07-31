package com.calliduscloud.scas.scim_services.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AuthenticationFilter will do the authentication and lets the rest of the
 * services do their job.
 */
@Component
@Order(1)
public class AuthenticationFilter implements Filter {
    static {
        System.out.println("I am loaded ");
    }
    private static final Logger LOG =
            LoggerFactory.getLogger(AuthenticationFilter.class);
    private static ThreadLocal<HttpServletResponse> responses
            = new ThreadLocal<>();
    private static ThreadLocal<HttpServletRequest> requests
            = new ThreadLocal<>();

    /**
     * This method is used to validate the token and sets necessary stuff.
     */
    final void validateJWTToken() {
        LOG.debug("Validating the JWT token");
    }

    @Override
    public final void init(final FilterConfig filterConfig)
            throws ServletException {
        LOG.info("INFO: Init method");
        LOG.debug("DEBUG: Init method");
    }

    @Override
    public final void doFilter(final ServletRequest request,
                               final ServletResponse response,
                               final FilterChain chain)
            throws IOException, ServletException {
        requests.set((HttpServletRequest) request);
        responses.set((HttpServletResponse) response);
        //call validateJWTToken() and set the User and Tenant
        LOG.info("Info: doFilter method");
        LOG.debug("DEBUG: doFilter method");
        chain.doFilter(request, response);
    }

    @Override
    public final void destroy() {
        LOG.debug("destroy method");
    }

    /**
     * getRequest will return the ServletRequest.
     * @return request object
     */
    public static HttpServletRequest getRequest() {
        return requests.get();
    }

    /**
     * getResponse will return the ServletResponse.
     * @return response object
     */
    public static HttpServletResponse getResponse() {
        return responses.get();
    }
}
