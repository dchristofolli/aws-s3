package com.dchristofolli.projects.awss3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ErrorModel fileSizeLimitExceededException(MaxUploadSizeExceededException e) {
        return ErrorModel.builder()
                .message("File size limit exceeded. Please add a file with a maximum size of 1MB")
                .error(e.getClass().getName())
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }
}
