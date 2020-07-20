package com.dchristofolli.projects.awss3;

import com.dchristofolli.projects.awss3.model.FileModel;
import com.dchristofolli.projects.awss3.model.ResponseModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Stub {
    public static ResponseModel responseModelStub() {
        List<FileModel> fileModelList = Collections.singletonList(FileModel.builder()
                .fileName("test.txt")
                .size(5)
                .build());
        return ResponseModel.builder()
                .bucket("bucket")
                .files(fileModelList)
                .build();
    }
}
