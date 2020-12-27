package ru.hse.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static ru.hse.web.Constants.ADMIN;
import static ru.hse.web.Constants.CLIENT;

@Component
@RequiredArgsConstructor
public class AuthorityInterceptor implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        var authHeader = Optional.ofNullable(req.getHeader("X_GRANT_ID")).orElse("-");
        if ((uri.contains("privilege") || uri.contains("user")) && req.getMethod().toUpperCase().equals("GET")) {
            if (authHeader.equals(ADMIN) || authHeader.equals(CLIENT)) {
                chain.doFilter(request, response);
            } else {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User does not have access to this resource");
            }
        } else {
            if (uri.contains("privilege")) {
                if (authHeader.equals(ADMIN)) {
                    chain.doFilter(request, response);
                } else {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User does not have access to this resource");
                }
            } else if (uri.contains("user")) {
                if (authHeader.equals(CLIENT)) {
                    chain.doFilter(request, response);
                } else {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User does not have access to this resource");
                }
            } else {
                chain.doFilter(request, response);
            }
        }
    }
}
