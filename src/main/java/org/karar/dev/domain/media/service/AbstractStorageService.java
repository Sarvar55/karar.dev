package org.karar.dev.domain.media.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractStorageService implements StorageService {

    protected final ContentTypeResolver contentTypeResolver;

    protected String resolveContentType(String filename) {
        return contentTypeResolver.resolve(filename);
    }
}

