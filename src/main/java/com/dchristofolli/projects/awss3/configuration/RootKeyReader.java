package com.dchristofolli.projects.awss3.configuration;

import com.dchristofolli.projects.awss3.exception.ApiException;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Slf4j
@Component
public class RootKeyReader {
    public static RootKeyModel getRootKey() {
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get("rootkey.csv"));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new CsvToBeanBuilder<RootKeyModel>(Objects.requireNonNull(reader))
                .withType(RootKeyModel.class)
                .withSeparator(';')
                .build()
                .parse().stream()
                .findFirst()
                .orElseThrow(() -> new ApiException("File not found", HttpStatus.NOT_FOUND));
    }
}
