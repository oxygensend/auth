package com.oxygensend.auth.port.adapter.in.rest.filters;

import com.oxygensend.auth.port.Ports;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;


@Profile(Ports.REST)
@Component
class RequestIdFilter implements Filter, Ordered {
    private final static String REQUEST_ID_HEADER = "Request-Id";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestId = request.getHeader(REQUEST_ID_HEADER) != null ?
                           request.getHeader(REQUEST_ID_HEADER) : "[" + RandomStringUtils.randomAlphabetic(10) + "]";
        MDC.put(REQUEST_ID_HEADER, requestId);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
