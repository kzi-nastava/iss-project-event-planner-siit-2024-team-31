package com.example.eventplanner.MVC.controller;

import com.example.eventplanner.controller.AuthenticationController;
import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.userDto.*;
import com.example.eventplanner.model.Role;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.service.AuthenticationService;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)  // Disable security filters for testing
@TestPropertySource(properties = {
        "SPRING_SERVER_PORT=8080"
})
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean private AuthenticationService authService;
    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;

    @Test
    void signup_Returns201AndMessage() throws Exception {
        mvc.perform(multipart("/api/auth/signup")
                        .param("email", "a@b.com")
                        .param("password", "pass")
                        .param("role", "USER"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("We sent you an email with a confirmation link. Please, check your email and confirm your registration."));
    }

    @Test
    void activateUser_ReturnsCommonMessage() throws Exception {
        doNothing().when(userService).activateUser(5L);

        mvc.perform(get("/api/auth/activate")
                        .param("id", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("User with id 5 has been activated"));
    }

    @Test
    void login_ReturnsTokenAndRole() throws Exception {
        User u = new User();
        Role role = new Role("USER");
        role.setId(1L);
        u.setRole(role);

        when(authService.login(any())).thenReturn(u);
        when(jwtService.generateToken(u)).thenReturn("jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.com\",\"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.tokenExpiresIn").value(3600));
    }

    @Test
    void sendRecoveryCode_ReturnsOk() throws Exception {
        when(authService.sendRecoveryCode(any(EmailDTO.class)))
                .thenReturn(new CommonMessageDTO("ok", null));

        mvc.perform(post("/api/auth/send-recovery-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    void verifyRecoveryCode_ReturnsOk() throws Exception {
        when(authService.verifyRecoveryCode(any(UserRecoveryCodeVerificationRequestDTO.class)))
                .thenReturn(new CommonMessageDTO("Code verified", null));

        mvc.perform(post("/api/auth/verify-recovery-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.com\",\"code\":\"1234\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Code verified"));
    }

    @Test
    void resetPassword_ReturnsOk() throws Exception {
        when(authService.resetPassword(any(UserResetPasswordRequestDTO.class)))
                .thenReturn(new CommonMessageDTO("Password reset successfully", null));

        mvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.com\",\"code\":\"1234\",\"newPassword\":\"newPass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successfully"));
    }

    @Test
    void deactivateUser_ReturnsOk() throws Exception {
        when(jwtService.extractUserEmailFromAuthorizationRequest(any()))
                .thenReturn("a@b.com");
        when(authService.deactivateUser(eq("a@b.com"), eq("pass")))
                .thenReturn(new CommonMessageDTO("User deactivated successfully", null));

        mvc.perform(post("/api/auth/deactivate")
                        .param("password", "pass")
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deactivated successfully"));
    }
}