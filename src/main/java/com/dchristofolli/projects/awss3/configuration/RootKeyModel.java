package com.dchristofolli.projects.awss3.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class RootKeyModel {
    private String AWSAccessKeyId;
    private String AWSSecretKey;
}
