package com.lkm.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class InternalAPIKeyInterceptor implements HandlerInterceptor {

    private final static String HEADER_NAME = "x-top-secret-id";
    private final static String HEADER_VALUE = "Dummy bava";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientSecret = request.getHeader(HEADER_NAME);

        if(clientSecret == null || !clientSecret.equals(HEADER_VALUE)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Access denied. Direct communication not allowed.");
            return false;
        }

        return true;
    }
}
