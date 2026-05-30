package org.karar.dev.domain.media.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.karar.dev.domain.media.service.ContentTypeResolver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TikaContentTypeResolver implements ContentTypeResolver {

    private final Tika tika;

    @Override
    public String resolve(String filename) {
        try {
            String detectedType = tika.detect(filename);
            if (detectedType != null) {
                return detectedType;
            }
        } catch (Exception e) {
            log.warn("Tika failed to detect content type for file {}: {}", filename, e.getMessage());
        }

        return "application/octet-stream";
    }
}
