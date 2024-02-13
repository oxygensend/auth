package com.oxygensend.auth.unit.context;

import com.oxygensend.auth.context.AuthController;
import com.oxygensend.auth.context.AuthService;
import com.oxygensend.auth.context.request.AuthenticationRequest;
import com.oxygensend.auth.context.request.RefreshTokenRequest;
import com.oxygensend.auth.context.request.RegisterRequest;
import com.oxygensend.auth.context.response.AuthenticationResponse;
import com.oxygensend.auth.domain.exception.UnauthorizedException;
import com.oxygensend.commons_jdk.exception.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    @Mock
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setValidator(new LocalValidatorFactoryBean())
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    public void testRegister_Successful() throws Exception {
        // Arrange
        AuthenticationResponse response = new AuthenticationResponse("accessToken", "refreshToken");
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        // Act & Assert
         mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"firstName\":\"John\"," +
                                "\"lastName\":\"Doe\"," +
                                "\"email\":\"test@example.com\"," +
                                "\"password\":\"password\"" +
                                "}"
                        )
                )
                 .andExpect(MockMvcResultMatchers.status().isCreated())
                 .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("accessToken"))
                 .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value("refreshToken"));

    }

    @Test
    public void testRegister_BadRequest() throws Exception {

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"firstName\":\"John\"," +
                                "\"email\":\"testcom\"" +
                                "}"
                        )
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void testAuthenticate_Successful() throws Exception {
        // Arrange
        AuthenticationResponse response = new AuthenticationResponse("accessToken", "refreshToken");

        when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/access_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"test@example.com\"," +
                                "\"password\":\"password\"}"
                        )
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("accessToken")).andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value("refreshToken"))
                .andDo(print());

    }

    @Test
    public void testAuthenticate_BadCredentials() throws Exception {
        // Arrange

        when(authService.authenticate(any(AuthenticationRequest.class))).thenThrow(new UnauthorizedException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/access_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"test@example.com\"," +
                                "\"password\":\"password\"}"
                        )
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Bad credentials"))
                .andDo(print());

    }

    @Test
    public void testRefreshToken_Successful() throws Exception {
        // Arrange
        AuthenticationResponse response = new AuthenticationResponse("accessToken", "refreshToken");
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/refresh_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"refresh_token\"}"
                        )
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("accessToken"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value("refreshToken"))
                .andDo(print());
    }





}
