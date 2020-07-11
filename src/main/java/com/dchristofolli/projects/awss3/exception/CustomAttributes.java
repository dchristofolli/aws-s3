package com.dchristofolli.projects.awss3.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
public class CustomAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributesMap = super.getErrorAttributes(webRequest, options);
        Throwable throwable = getError(webRequest);
        if (throwable instanceof ResponseStatusException) {
            var responseStatusException = (ResponseStatusException) throwable;
            errorAttributesMap.put("message", responseStatusException.getMessage());
            errorAttributesMap.put("developerMessage", "A ResponseStatusException Happened");
        }
        return errorAttributesMap;
    }
}
