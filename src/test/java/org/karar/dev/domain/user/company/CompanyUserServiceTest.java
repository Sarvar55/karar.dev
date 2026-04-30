package org.karar.dev.domain.user.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.user.UserService;
import org.karar.dev.domain.user.company.dto.CompanyUserResponse;
import org.karar.dev.domain.user.company.dto.CompanyUserUpdateRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyUserServiceTest {

    @Mock
    private CompanyUserRepository companyUserRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private CompanyUserService companyUserService;

    private UUID id;
    private static final String email = "company@karar.dev";
    private static final String password = "password";
    private static final String companyName = "karar.dev";

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
    }

    private CompanyUser createCompanyUser() {
        CompanyUser companyUser = new CompanyUser();
        companyUser.setId(id);
        companyUser.setEmail(email);
        companyUser.setCompanyName(companyName);
        return companyUser;
    }

    private void mockFindByIdCompanyUser(Optional<CompanyUser> companyUser) {
        when(companyUserRepository.findById(id))
                .thenReturn(companyUser);
    }

    private void assertsCompanyUsers(BaseResponse<PageResponse<CompanyUserResponse>> response) {
        response.getData()
                .getContent()
                .forEach(companyUser ->
                        assertsCompanyUser(BaseResponse.success(companyUser)));
    }

    private void assertsCompanyUser(BaseResponse<CompanyUserResponse> response) {
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(id, response.getData().id());
        assertEquals(email, response.getData().email());
        assertEquals(companyName, response.getData().companyName());
    }


    @Nested
    @DisplayName("Get All")
    class GetAll {

        @Test
        @DisplayName("Should return all companies")
        void getAllShouldReturnAllCompanies() {
            Pageable pageable = PageRequest.of(0, 1);
            CompanyUser companyUser = createCompanyUser();

            when(companyUserRepository.findAll(pageable)).
                    thenReturn(new PageImpl<>(List.of(companyUser), pageable, 1));

            BaseResponse<PageResponse<CompanyUserResponse>> response =
                    companyUserService.getAll(pageable);

            assertsCompanyUsers(response);

            verify(companyUserRepository).findAll(pageable);

        }
    }

    @Nested
    class GetCompanyById {
        @Test
        @DisplayName("Should return company by ID successfully")
        void getCompanyByIdShouldReturnCompanyWhenFound() {
            CompanyUser companyUser = createCompanyUser();
            mockFindByIdCompanyUser(Optional.of(companyUser));

            BaseResponse<CompanyUserResponse> response =
                    companyUserService.getCompanyById(id);

            assertsCompanyUser(response);
            verify(companyUserRepository).findById(id);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when company not found")
        void getCompanyByIdShouldThrowResourceNotFoundExceptionWhenCompanyDoesNotExist() {
            mockFindByIdCompanyUser(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> companyUserService.getCompanyById(id));
            verify(companyUserRepository).findById(id);

        }
    }

    @Test
    @DisplayName("Should create company successfully")
    void updateShouldUpdateAndReturnCompanyWhenFound() {
        CompanyUser companyUser = createCompanyUser();
        var updateEmail = "email@update.com";
        var updateCompanyName = "update.com";
        CompanyUserUpdateRequest updateRequest
                = new CompanyUserUpdateRequest(updateEmail, updateCompanyName);

        when(companyUserRepository.findById(id))
                .thenReturn(Optional.of(companyUser));

        when(companyUserRepository.save(companyUser))
                .thenReturn(companyUser);

        BaseResponse<CompanyUserResponse> response =
                companyUserService.update(id, updateRequest);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(id, response.getData().id());
        assertEquals(updateEmail, response.getData().email());
        assertEquals(updateCompanyName, response.getData().companyName());

        verify(companyUserRepository).findById(id);
        verify(companyUserRepository).save(companyUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when company not found")
    void updateShouldThrowResourceNotFoundExceptionWhenCompanyUserDoesntExist() {
        var updateEmail = "email@update.com";
        var updateCompanyName = "update.com";
        CompanyUserUpdateRequest updateRequest
                = new CompanyUserUpdateRequest(updateEmail, updateCompanyName);
        when(companyUserRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> companyUserService.update(id, updateRequest));

        verify(companyUserRepository).findById(id);
        verify(companyUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete company successfully")
    void deleteShouldDeleteCompanyWhenFound() {
        when(companyUserRepository.existsById(id))
                .thenReturn(true);

        BaseResponse<Void> response = companyUserService.delete(id);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        verify(companyUserRepository).deleteById(id);
        verify(companyUserRepository).existsById(id);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when company not found")
    void deleteShouldThrowResourceNotFoundExceptionWhenCompanyUserDoesntExist() {
        when(companyUserRepository.existsById(id))
                .thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> companyUserService.delete(id));
        verify(companyUserRepository).existsById(id);
        verify(companyUserRepository, never()).deleteById(id);
    }
}