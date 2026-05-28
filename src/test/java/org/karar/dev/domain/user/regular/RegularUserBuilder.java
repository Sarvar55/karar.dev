package org.karar.dev.domain.user.regular;
import org.karar.dev.domain.user.regular.entity.RegularUser;

import java.util.UUID;

public class RegularUserBuilder {
    private UUID id = UUID.randomUUID();
    private String email = "user@karar.dev";
    private String username = "user";

    public static RegularUserBuilder user() {
        return new RegularUserBuilder();
    }

    public RegularUserBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public RegularUserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public RegularUserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public RegularUser build() {
        RegularUser user = new RegularUser();
        user.setId(id);
        user.setEmail(email);
        user.setUsername(username);
        return user;
    }
}
