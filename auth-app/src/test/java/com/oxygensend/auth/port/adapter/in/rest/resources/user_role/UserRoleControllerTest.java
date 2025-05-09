package com.oxygensend.auth.port.adapter.in.rest.resources.user_role;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxygensend.auth.application.identity.UserRoleService;
import com.oxygensend.auth.application.identity.exception.UnexpectedRoleException;
import com.oxygensend.auth.application.identity.exception.UserNotFoundException;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.port.adapter.in.rest.exception.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.UUID;

import com.oxygensend.common.domain.model.DomainModelValidationException;

@ExtendWith(MockitoExtension.class)
class UserRoleControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @Mock
    private UserRoleService userRoleService;

    @InjectMocks
    private UserRoleController userRoleController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userRoleController)
                                 .setValidator(new LocalValidatorFactoryBean())
                                 .setControllerAdvice(new ApiExceptionHandler())
                                 .build();
    }

    @Test
    void shouldAddRoleToUser_whenValidRequest() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        String roleName = "ADMIN";
        UserRoleRequest request = new UserRoleRequest(userId, roleName);

        doNothing().when(userRoleService).addRoleToUser(any(UserId.class), any(Role.class));

        // When & Then
        mockMvc.perform(post("/v1/user-roles/add")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Role added successfully")));

        verify(userRoleService).addRoleToUser(new UserId(userId), new Role(roleName));
    }

    @Test
    void shouldRemoveRoleFromUser_whenValidRequest() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        String roleName = "ADMIN";
        UserRoleRequest request = new UserRoleRequest(userId, roleName);

        doNothing().when(userRoleService).removeRoleFromUser(any(UserId.class), any(Role.class));

        // When & Then
        mockMvc.perform(post("/v1/user-roles/remove")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Role removed successfully")));

        verify(userRoleService).removeRoleFromUser(new UserId(userId), new Role(roleName));
    }

    @Test
    void shouldReturnBadRequest_whenAddRoleWithInvalidRole() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        String invalidRole = "INVALID_ROLE";
        UserRoleRequest request = new UserRoleRequest(userId, invalidRole);

        doThrow(new UnexpectedRoleException(new Role(invalidRole)))
            .when(userRoleService).addRoleToUser(any(UserId.class), any(Role.class));

        // When & Then
        mockMvc.perform(post("/v1/user-roles/add")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequest_whenRemoveRoleWithInvalidRole() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        String invalidRole = "INVALID_ROLE";
        UserRoleRequest request = new UserRoleRequest(userId, invalidRole);

        doThrow(new UnexpectedRoleException(new Role(invalidRole)))
            .when(userRoleService).removeRoleFromUser(any(UserId.class), any(Role.class));

        // When & Then
        mockMvc.perform(post("/v1/user-roles/remove")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequest_whenDomainValidationException() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        String roleName = "USER";
        UserRoleRequest request = new UserRoleRequest(userId, roleName);

        doThrow(new DomainModelValidationException("Invalid domain data"))
            .when(userRoleService).addRoleToUser(any(UserId.class), any(Role.class));

        // When & Then
        mockMvc.perform(post("/v1/user-roles/add")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message", is("Invalid domain data")));
    }

    @Test
    void shouldReturnBadRequest_whenUserNotFound() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        String roleName = "USER";
        UserRoleRequest request = new UserRoleRequest(userId, roleName);
        UserId userIdObj = new UserId(userId);

        doThrow(UserNotFoundException.withId(userIdObj))
            .when(userRoleService).addRoleToUser(any(UserId.class), any(Role.class));

        // When & Then
        mockMvc.perform(post("/v1/user-roles/add")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequest_whenInvalidRequestBody() throws Exception {
        // Given
        UserRoleRequest request = new UserRoleRequest(null, "");

        // When & Then
        mockMvc.perform(post("/v1/user-roles/add")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }
}
