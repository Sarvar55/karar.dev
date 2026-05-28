package org.karar.dev.domain.tag;
import org.karar.dev.domain.tag.entity.Tag;

import java.util.UUID;

public class TagBuilder {
    private UUID id = UUID.randomUUID();
    private String name = "Java";

    public static TagBuilder tag() {
        return new TagBuilder();
    }

    public TagBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public TagBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public Tag build() {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        return tag;
    }

}
