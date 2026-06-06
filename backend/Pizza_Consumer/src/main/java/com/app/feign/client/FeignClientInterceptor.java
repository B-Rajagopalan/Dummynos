package com.app.feign.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private final static String HEADER_NAME = "x-top-secret-id";
    private final static String HEADER_VALUE = "Dummy bava";
    @Override
    public void apply(RequestTemplate template) {
        template.header(HEADER_NAME, HEADER_VALUE);
    }
}
