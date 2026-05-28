package org.karar.dev.domain.user.company.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.karar.dev.common.enums.Role;
import org.karar.dev.domain.user.entity.User;


@Entity
@Table(name = "company_users")
@Getter
@Setter
@NoArgsConstructor
public class CompanyUser extends User {

    private String companyName;

    public CompanyUser(String email, String password, String companyName) {
        super(email, password, Role.COMPANY);
        this.companyName = companyName;
    }
}
