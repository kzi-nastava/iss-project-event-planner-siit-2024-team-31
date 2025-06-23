package com.example.eventplanner.JUnit5.service;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.userDto.*;
import com.example.eventplanner.exception.exceptions.auth.EmailAlreadyUsedException;
import com.example.eventplanner.exception.exceptions.auth.UserNotActivatedException;
import com.example.eventplanner.model.Role;
import com.example.eventplanner.model.user.PasswordResetToken;
import com.example.eventplanner.model.user.*;
import com.example.eventplanner.repository.*;
import com.example.eventplanner.service.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock      private UserRepository userRepo;
    @Mock      private RoleRepository roleRepo;
    @Mock      private PasswordEncoder passwordEncoder;
    @Mock      private AuthenticationManager authManager;
    @Mock      private PhotoService photoService;
    @Mock      private EmailService emailService;
    @Mock      private PasswordResetTokenRepo tokenRepo;
    @Mock      private ReservationService reservationService;
    @Mock      private EventService eventService;
    @InjectMocks private AuthenticationService svc;

    private UserRegisterRequestDTO signupDto;
    private User user;

    @BeforeEach
    void setUp() {
        signupDto = new UserRegisterRequestDTO();
        signupDto.setEmail("a@b.com");
        signupDto.setPassword("pass");
        signupDto.setRole("USER");

        user = new User();
        user.setId(42L);
        user.setEmail("a@b.com");
        user.setActive(true);
        user.setPassword("encoded");
    }

    @Test
    void signup_Success() {

        Role mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName("USER");

        when(userRepo.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(roleRepo.findByName("USER")).thenReturn(mockRole);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        svc.signup(signupDto);

        verify(userRepo).saveAndFlush(argThat(u ->
                u.getEmail().equals("a@b.com")
                        && u.getPassword().equals("encoded")
        ));
    }

    @Test
    void signup_EmailAlreadyUsed() {
        when(userRepo.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> svc.signup(signupDto))
                .isInstanceOf(EmailAlreadyUsedException.class);
    }

    @Test
    void login_Success() {
        UserLoginRequestDTO dto = new UserLoginRequestDTO();
        dto.setEmail("a@b.com");
        dto.setPassword("pass");

        when(userRepo.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        user.setActive(true);

        svc.login(dto);
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_NotActivated() {
        UserLoginRequestDTO dto = new UserLoginRequestDTO();
        dto.setEmail("a@b.com");
        dto.setPassword("pass");
        user.setActive(false);
        when(userRepo.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> svc.login(dto))
                .isInstanceOf(UserNotActivatedException.class);
    }

    @Test
    void sendRecoveryCode_Success() {
        EmailDTO req = new EmailDTO(); req.setEmail("a@b.com");
        when(userRepo.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        user.setActive(true);
        when(tokenRepo.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommonMessageDTO resp = svc.sendRecoveryCode(req);
        assertThat(resp.getMessage()).contains("Recovery code sent");

        verify(emailService).sendTestEmail(
                argThat(details -> "a@b.com".equals(details.getTo()))
        );
    }

    @Test
    void verifyRecoveryCode_Success() {
        String code = "1234";
        String hash = DigestUtils.sha256Hex(code);
        UserRecoveryCodeVerificationRequestDTO req = new UserRecoveryCodeVerificationRequestDTO();
        req.setEmail("a@b.com"); req.setCode(code);

        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(42L);
        token.setTokenHash(hash);
        token.setUsed(false);
        token.setExpiryAt(Instant.now().plusSeconds(3600));

        when(userRepo.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        user.setActive(true);
        when(tokenRepo.findByTokenHash(hash)).thenReturn(Optional.of(token));

        CommonMessageDTO resp = svc.verifyRecoveryCode(req);
        assertThat(resp.getMessage()).contains("verified successfully");
        assertThat(token.isUsed()).isTrue();
    }

    @Test
    void resetPassword_Success() {
        String email = "a@b.com";
        String code = "1234";
        String hash = DigestUtils.sha256Hex(code);
        String newPass = "newPass";

        User user = new User();
        user.setId(42L);
        user.setEmail(email);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(42L);
        token.setTokenHash(hash);
        token.setUsed(true);
        token.setExpiryAt(Instant.now().plusSeconds(3600));
        when(tokenRepo.findByTokenHash(hash)).thenReturn(Optional.of(token));

        when(passwordEncoder.encode(newPass)).thenReturn("encodedNewPass");

        UserResetPasswordRequestDTO dto = new UserResetPasswordRequestDTO();
        dto.setEmail(email);
        dto.setCode(code);
        dto.setNewPassword(newPass);

        var resp = svc.resetPassword(dto);

        assertThat(resp.getMessage()).isEqualTo("Password reset successfully");

        ArgumentCaptor<User> userCap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userCap.capture());
        assertThat(userCap.getValue().getPassword()).isEqualTo("encodedNewPass");
    }

    @Test
    void resetPassword_NotVerifiedCode_Throws() {
        String email = "a@b.com";
        String code = "0000";
        String hash = DigestUtils.sha256Hex(code);

        User user = new User();
        user.setId(42L);
        user.setEmail(email);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(42L);
        token.setTokenHash(hash);
        token.setUsed(false);  // ещё не верифицирован
        token.setExpiryAt(Instant.now().plusSeconds(3600));
        when(tokenRepo.findByTokenHash(hash)).thenReturn(Optional.of(token));

        UserResetPasswordRequestDTO dto = new UserResetPasswordRequestDTO();
        dto.setEmail(email);
        dto.setCode(code);
        dto.setNewPassword("whatever");

        assertThatThrownBy(() -> svc.resetPassword(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Recovery code has not been verified");
    }

    @Test
    void deactivateUser_Success() {
        String email = "user@example.com";
        String rawPass = "pass";
        String encodedPass = "enc";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPass);
        user.setActive(true);
        // роль, отличная от ADMIN, PUP, OD
        Role role = new Role(); role.setName("USER");
        user.setRole(role);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPass, encodedPass)).thenReturn(true);

        var resp = svc.deactivateUser(email, rawPass);

        assertThat(resp.getMessage()).isEqualTo("User deactivated successfully");
        assertThat(user.isActive()).isFalse();
        verify(userRepo).save(user);
    }

    @Test
    void deactivateUser_IncorrectPassword_Throws() {
        String email = "user@example.com";
        String rawPass = "wrong";
        String encodedPass = "enc";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPass);
        user.setActive(true);
        user.setRole(new Role()); // любая роль

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPass, encodedPass)).thenReturn(false);

        assertThatThrownBy(() -> svc.deactivateUser(email, rawPass))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Incorrect password");
    }
}