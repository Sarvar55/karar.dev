package org.karar.dev.domain.user.regular.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegularUserUpdateRequest(
    @NotBlank @Email String email,
    @NotBlank String username,
    String photoUrl,
    String bio,
    String location,
    String jobTitle,
    String experience,
    java.util.List<String> skills,
    String openTo,
    String website,
    String githubUrl,
    String twitterUrl
) {
}

