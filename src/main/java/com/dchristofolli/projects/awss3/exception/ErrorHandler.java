package com.dchristofolli.projects.awss3.exception;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.mq.model.NotFoundException;
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SdkClientException.class)
    public ErrorModel sdkClientException(SdkClientException e) {
        return ErrorModel.builder()
                .message("No such file or directory")
                .error(e.getClass().getName())
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AmazonServiceException.class)
    public ErrorModel amazonException(AmazonServiceException e) {
        return ErrorModel.builder()
                .message("Amazon Error")
                .error(e.getClass().getName())
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorModel notFoundException(ApiException e) {
        return ErrorModel.builder()
                .message("File not found")
                .error(e.getClass().getName())
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorModel handleException(Exception e) {
        return ErrorModel.builder()
                .message("Unexpected Error")
                .error(e.getClass().getName())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}
