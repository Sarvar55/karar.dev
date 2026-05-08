package org.karar.dev.domain.user.company;

import java.util.UUID;

public class CompanyUserBuilder {
    private UUID id = UUID.randomUUID();
    private String email = "karar@gmail.com";
    private String companyName = "karar.dev";

    public static CompanyUserBuilder companyUser() {
        return new CompanyUserBuilder();
    }

    public CompanyUserBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public CompanyUserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public CompanyUserBuilder withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public CompanyUser build() {
        CompanyUser companyUser = new CompanyUser();
        companyUser.setId(id);
        companyUser.setEmail(email);
        companyUser.setCompanyName(companyName);
        return companyUser;
    }

}
