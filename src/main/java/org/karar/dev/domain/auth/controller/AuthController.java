package org.karar.dev.domain.auth.controller;
import org.karar.dev.domain.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karar.dev.domain.auth.dto.*;
import org.karar.dev.common.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

        private final AuthService authService;

        @Operation(summary = "Register a new user or company", description = "Endpoint for registering both regular users and companies.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Registration successful", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input or missing role-specific fields", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @PostMapping(value = "/register", produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
                RegisterResponse registerResponse = authService.register(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.success(registerResponse, HttpStatus.CREATED));
        }

        @Operation(summary = "Login with email and password", description = "Authenticates the user and returns a JWT access token.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid email or password", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Account locked or disabled", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @PostMapping(value = "/login", produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
                AuthResponse authResponse = authService.login(request);
                return ResponseEntity.ok(BaseResponse.success(authResponse));
        }

        @Operation(summary = "Refresh access token", description = "Accepts a valid refresh token and returns a new access token and refresh token pair.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @PostMapping(value = "/refresh", produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<AuthResponse>> refreshToken(
                        @Valid @RequestBody RefreshTokenRequest request) {
                AuthResponse authResponse = authService.refreshToken(request);
                return ResponseEntity.ok(BaseResponse.success(authResponse));
        }

        @Operation(summary = "Verify email address", description = "Verifies user email using the token sent via email.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid or expired token")
        })
        @GetMapping(value = "/verify", produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<String>> verifyEmail(@RequestParam String token) {
                String message = authService.verifyEmail(token);
                return ResponseEntity.ok(BaseResponse.success(message));
        }

        @Operation(summary = "Resend verification email", description = "Resends verification email to the user. Old token is invalidated.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Verification email resent"),
                        @ApiResponse(responseCode = "400", description = "Invalid email or already verified")
        })
        @PostMapping(value = "/resend-verification", produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<String>> resendVerification(
                        @Valid @RequestBody ResendVerificationRequest request) {
                String message = authService.resendVerification(request);
                return ResponseEntity.ok(BaseResponse.success(message));
        }

        // ===================== OTP Login =====================

        @Operation(summary = "Send OTP code", description = "Sends a one-time password to the user's email for passwordless login.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
                        @ApiResponse(responseCode = "401", description = "Email not found")
        })
        @PostMapping(value = "/otp/send", produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<String>> sendOtp(@Valid @RequestBody OtpRequest request) {
                String message = authService.sendOtp(request);
                return ResponseEntity.ok(BaseResponse.success(message));
        }

        @Operation(summary = "Verify OTP and login", description = "Validates the OTP code and returns JWT tokens.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OTP verified, login successful"),
                        @ApiResponse(responseCode = "401", description = "Invalid or expired OTP")
        })
        @PostMapping(value = "/otp/verify", produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<AuthResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
                AuthResponse authResponse = authService.loginWithOtp(request);
                return ResponseEntity.ok(BaseResponse.success(authResponse));
        }
}

