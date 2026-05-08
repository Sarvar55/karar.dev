package org.karar.dev.domain.user.company;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.annotation.UnitTest;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.extensions.CompanyUserParameterResolver;
import org.karar.dev.domain.user.UserService;
import org.karar.dev.domain.user.company.dto.CompanyUserResponse;
import org.karar.dev.domain.user.company.dto.CompanyUserUpdateRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@UnitTest
@ExtendWith(CompanyUserParameterResolver.class)
class CompanyUserServiceTest {
    //should + ExpectedBehavior + when + Condition
    @Mock
    private CompanyUserRepository companyUserRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private CompanyUserService companyUserService;

    @Nested
    @DisplayName("getAll()")
    class GetAll {

        @Test
        @DisplayName("Should return all companies")
        void shouldReturnPaginatedCompaniesWhenCompaniesExists(CompanyUser company) {
            Pageable pageable = PageRequest.of(0, 1);


            when(companyUserRepository.findAll(pageable)).
                    thenReturn(new PageImpl<>(List.of(company), pageable, 1));

            BaseResponse<PageResponse<CompanyUserResponse>> response =
                    companyUserService.getAll(pageable);

            assertThat(response)
                    .extracting(BaseResponse::getStatus)
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getData().getContent())
                    .hasSize(1)
                    .first()
                    .extracting(CompanyUserResponse::id, CompanyUserResponse::email, CompanyUserResponse::companyName)
                    .containsExactly(company.getId(), company.getEmail(), company.getCompanyName());

            verify(companyUserRepository).findAll(pageable);

        }
    }

    @Nested
    class GetById {
        @Test
        @DisplayName("Should return company by ID successfully")
        void shouldReturnCompanyWhenCompanyExists(CompanyUser company) {
            UUID id = company.getId();
            when(companyUserRepository.findById(id))
                    .thenReturn(Optional.of(company));

            BaseResponse<CompanyUserResponse> response =
                    companyUserService.getCompanyById(id);

            assertThat(response)
                    .extracting(BaseResponse::getStatus)
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getData())
                    .extracting(CompanyUserResponse::id, CompanyUserResponse::email, CompanyUserResponse::companyName)
                    .containsExactly(company.getId(), company.getEmail(), company.getCompanyName());

            verify(companyUserRepository).findById(id);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when company not found")
        void shouldThrowResourceNotFoundExceptionWhenCompanyUserDoesntExist() {
            when(companyUserRepository.findById(any()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyUserService.getCompanyById(any()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(companyUserRepository).findById(any());

        }
    }

    @Nested
    @DisplayName("Update")
    class Update {
        @Test
        @DisplayName("Should create company successfully")
        void shouldUpdateCompanyWhenValidRequest(CompanyUser company) {
            var updateEmail = "email@update.com";
            var updateCompanyName = "update.com";
            UUID id = company.getId();

            CompanyUserUpdateRequest updateRequest
                    = new CompanyUserUpdateRequest(updateEmail, updateCompanyName);

            when(companyUserRepository.findById(id))
                    .thenReturn(Optional.of(company));

            when(companyUserRepository.save(any()))
                    .thenReturn(company);

            BaseResponse<CompanyUserResponse> response =
                    companyUserService.update(id, updateRequest);

            assertThat(response.getData().email()).isEqualTo(updateEmail);
            assertThat(response.getData().companyName()).isEqualTo(updateCompanyName);
            assertThat(response.getData().id()).isEqualTo(id);

            verify(companyUserRepository).findById(id);
            verify(companyUserRepository).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when company not found")
        void shouldThrowResourceNotFoundExceptionWhenCompanyUserDoesntExist() {
            final UUID id = UUID.randomUUID();
            when(companyUserRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyUserService.update(id, any()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(companyUserRepository).findById(id);
            verify(companyUserRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete")
    class Delete {
        @Test
        @DisplayName("Should delete company successfully")
        void shouldDeleteCompanyWhenCompanyExists() {
            final UUID id = UUID.randomUUID();
            when(companyUserRepository.existsById(id))
                    .thenReturn(true);

            BaseResponse<Void> response = companyUserService.delete(id);

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);

            verify(companyUserRepository).deleteById(id);
            verify(companyUserRepository).existsById(id);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when company not found")
        void shouldThrowResourceNotFoundExceptionWhenCompanyUserDoesntExist() {
            when(companyUserRepository.existsById(any()))
                    .thenReturn(false);

            assertThatThrownBy(() -> companyUserService.delete(any()))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(companyUserRepository).existsById(any());
            verify(companyUserRepository, never()).deleteById(any());
        }
    }
}