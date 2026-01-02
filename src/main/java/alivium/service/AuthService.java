package alivium.service;

import alivium.domain.entity.User;
import alivium.domain.entity.Voucher;
import alivium.domain.repository.UserRepository;
import alivium.domain.repository.VoucherRepository;
import alivium.exception.AlreadyExistsException;
import alivium.exception.BusinessException;
import alivium.exception.InvalidInputException;
import alivium.exception.NotFoundException;
import alivium.mapper.UserMapper;
import alivium.model.dto.request.*;
import alivium.model.dto.response.AuthResponse;
import alivium.model.dto.response.MessageResponse;
import alivium.model.enums.AuthProvider;
import alivium.model.enums.NotificationTemplate;
import alivium.model.enums.UserStatus;
import alivium.security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final EmailService emailService;
    private final GoogleAuthService googleAuthService;
    private final VoucherService voucherService;

    @Transactional
    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String verificationCode =generateVerificationCode();
        redisService.saveVerificationCode(request.getEmail(), verificationCode);

        emailService.sendVerificationCode(user.getEmail(), verificationCode);

        return new MessageResponse("Registration successful! Please check your email for verification code.");
    }

    @Transactional
    public AuthResponse verifyUser(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User with email: " + request.getEmail() + " not found"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new BusinessException("Email is already verified");
        }

        if (!redisService.verifyCode(user.getEmail(), request.getCode())) {
            throw new BusinessException("Invalid or expired verification code");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        redisService.deleteVerificationCode(user.getEmail());

        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());

        voucherService.sendWelcomeNotificationWithVoucher(user);

        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toResponse(user))
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User with email: " + request.getEmail() + " not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new NotFoundException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.UNVERIFIED) {
            throw new BusinessException("Please verify your email before logging in");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("User is not active");
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toResponse(user))
                .build();
    }

    @Transactional
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        GoogleIdToken.Payload payload = googleAuthService.verifyGoogleToken(request.getIdToken());

        if (payload == null) {
            throw new InvalidInputException("Invalid Google token");
        }

        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        String profileImage = (String) payload.get("picture");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    String encodedPassword = passwordEncoder.encode("GOOGLE_AUTH_" + System.currentTimeMillis());
                    User newUser = userMapper.toGoogleUser(email, firstName, lastName, profileImage, encodedPassword);
                    return userRepository.save(newUser);
                });

        if (user.getAuthProvider() == AuthProvider.LOCAL) {
            user.setAuthProvider(AuthProvider.GOOGLE);
            user.setProfileImage(profileImage);
            userRepository.save(user);
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toResponse(user))
                .build();
    }

    @Transactional
    public MessageResponse logout(String accessToken, String refreshToken) {
        try {
            long accessExpiration = jwtService.extractExpiration(accessToken).getTime() - System.currentTimeMillis();
            if (accessExpiration > 0) {
                redisService.blacklistToken(accessToken, accessExpiration);
            }

            long refreshExpiration = jwtService.extractExpiration(refreshToken).getTime() - System.currentTimeMillis();
            if (refreshExpiration > 0) {
                redisService.blacklistToken(refreshToken, refreshExpiration);
            }

            return new MessageResponse("Logged out successfully");
        } catch (Exception e) {
            throw new InvalidInputException("Invalid tokens");
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            if (redisService.isBlacklisted(request.getRefreshToken())) {
                throw new BusinessException("Token has been revoked");
            }

            String email = jwtService.extractEmail(request.getRefreshToken());

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User not found"));

            if (!jwtService.validateToken(request.getRefreshToken(), user.getEmail())) {
                throw new IllegalArgumentException("Invalid refresh token");
            }

            String accessToken = jwtService.generateToken(user.getEmail());
            String refreshToken = jwtService.generateRefreshToken(user.getEmail());


            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(userMapper.toResponse(user))
                    .build();
        } catch (Exception e) {
            throw new InvalidInputException("Invalid refresh token");
        }
    }

    @Transactional(readOnly = true)
    public MessageResponse resendVerificationCode(ResendVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User with email: " + request.getEmail() + " not found"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new BusinessException("Email is already verified");
        }

        String verificationCode = generateVerificationCode();
        redisService.saveVerificationCode(user.getEmail(), verificationCode);

        emailService.sendVerificationCode(request.getEmail(), verificationCode);
        return new MessageResponse("Resend verification code successful.");
    }

    @Transactional(readOnly = true)
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + request.getEmail()));

        if (user.getStatus() == UserStatus.UNVERIFIED) {
            throw new IllegalStateException("Please verify your email first");
        }

        String resetCode = generateVerificationCode();
        redisService.savePasswordResetCode(request.getEmail(), resetCode);

        emailService.sendPasswordResetEmail(request.getEmail(), resetCode);

        return new MessageResponse("Password reset code has been sent to your email.");
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + request.getEmail()));

        if (!redisService.verifyPasswordResetCode(request.getEmail(), request.getCode())) {
            throw new BusinessException("Reset password code is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        redisService.deletePasswordResetCode(request.getEmail());

        return new MessageResponse("Password has been reset successfully. You can now login with your new password.");
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        return String.valueOf(code);
    }
}
