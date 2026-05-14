package org.karar.dev.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SqlFileReader {

    public String read(String path) {
        try {
            log.debug("started Reading SQL file: {}", path);
            return new String(
                    getClass()
                            .getResourceAsStream(path)
                            .readAllBytes());
        } catch (IOException e) {
            log.error("Error reading SQL file: {}", path, e);
            throw new RuntimeException("Error reading SQL file: " + path + e);
        }
    }
}