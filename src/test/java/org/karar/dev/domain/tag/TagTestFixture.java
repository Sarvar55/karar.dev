package org.karar.dev.domain.tag;

import java.util.UUID;

public abstract class TagTestFixture {
    protected UUID id() {
        return UUID.randomUUID();
    }
}
