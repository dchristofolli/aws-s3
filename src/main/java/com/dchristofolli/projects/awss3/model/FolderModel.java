package com.dchristofolli.projects.awss3.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FolderModel {
    String folderName;
    List<String> files;
    private Integer quantity;
    private String totalFileSize;
}
