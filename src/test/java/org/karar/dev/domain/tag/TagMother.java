package org.karar.dev.domain.tag;
import org.karar.dev.domain.tag.entity.Tag;

import java.util.UUID;

public class TagMother {
    public static Tag javaTag() {
        return TagBuilder.tag()
                .withName("java")
                .build();
    }

    public static Tag springTag() {
        return TagBuilder.tag()
                .withName("spring")
                .build();
    }

    public static Tag existingTag(UUID id) {
        return TagBuilder.tag()
                .withId(id)
                .withName("existing")
                .build();
    }
}
