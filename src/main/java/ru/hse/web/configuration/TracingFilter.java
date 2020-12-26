package ru.hse.web.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class TracingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        var excludeAuth = new ArrayList<>(Arrays.asList("api-docs", "configuration", "swagger", "webjars", "error"));
        AtomicBoolean exclude = new AtomicBoolean(false);

        log.info("-------------------------------------------------------------------------------------------");
        log.info(" /" + req.getMethod());
        log.info(" Request: " + req.getRequestURI());
        log.info("-------------------------------------------------------------------------------------------");

        var uri = req.getRequestURI();

        excludeAuth.forEach(ex -> {
            if (uri.contains(ex)) {
                exclude.set(true);
            }
        });
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.warn("Auth filter initialization");
    }
}