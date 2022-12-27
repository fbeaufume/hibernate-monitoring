package com.adeliosys.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessLogFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        long duration = -System.currentTimeMillis();

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            duration += System.currentTimeMillis();

            // Get the complete URL
            String url = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                url += '?' + queryString;
            }

            LOGGER.info("Served {} '{}' as {} in {} ms", request.getMethod(), url, response.getStatus(), duration);
        }
    }
}
