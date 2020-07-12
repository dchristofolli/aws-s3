package com.dchristofolli.projects.awss3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class ResponseModel {
    private String bucketName;
    private List<FileModel> fileModelList;
    private Integer quantity;
    private String totalFileSize;
}
