package com.dchristofolli.projects.awss3.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
class ErrorModel {
    String message;
    String error;
    HttpStatus status;
}
