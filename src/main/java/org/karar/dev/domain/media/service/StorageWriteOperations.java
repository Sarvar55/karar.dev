package org.karar.dev.domain.media.service;

import java.io.InputStream;

public interface StorageWriteOperations {

    String upload(InputStream inputStream, String folder, String filename);

    void delete(String objectName);

}

