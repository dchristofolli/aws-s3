package com.dchristofolli.projects.awss3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class FileModel {
    private String fileName;
    private long size;
}
