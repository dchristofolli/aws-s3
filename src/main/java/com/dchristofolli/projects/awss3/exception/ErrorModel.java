package com.dchristofolli.projects.awss3.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
class ErrorModel {
    String message;
    String error;
    HttpStatus status;
}
