package org.karar.dev.domain.media.service;

import java.io.InputStream;

public interface StorageReadOperations {

    InputStream download(String objectName);

    boolean exists(String objectName);

}
