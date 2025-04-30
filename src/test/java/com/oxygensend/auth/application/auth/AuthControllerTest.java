//package com.oxygensend.auth.application.auth;
//
//import com.oxygensend.auth.application.settings.LoginProvider;
//import com.oxygensend.auth.ui.auth.AuthController;
//import com.oxygensend.auth.ui.resources.auth.request.AuthenticationRequest;
//import com.oxygensend.auth.ui.resources.auth.request.RefreshTokenRequest;
//import com.oxygensend.auth.ui.resources.auth.request.RegisterRequest;
//import com.oxygensend.auth.ui.resources.auth.response.AuthenticationResponse;
//import com.oxygensend.auth.ui.resources.auth.response.RegisterResponse;
//import com.oxygensend.auth.ui.resources.auth.response.ValidationResponse;
//import com.oxygensend.auth.helper.ValidationResponseMother;
//import com.oxygensend.commons_jdk.exception.ApiExceptionHandler;
//import java.util.List;
//import java.util.UUID;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//
//@ExtendWith(MockitoExtension.class)
//public class AuthControllerTest {
//    @InjectMocks
//    private AuthController authController;
//
//    private MockMvc mockMvc;
//    @Mock
//    private AuthService authService;
//
//    @BeforeEach
//    public void setUp() {
//        identityProvider = mock(LoginProvider.class);
//        mockMvc = MockMvcBuilders.standaloneSetup(authController)
//                                 .setValidator(new LocalValidatorFactoryBean())
//                                 .setControllerAdvice(new ApiExceptionHandler())
//                                 .build();
//    }
//
//    @Test
//    public void testRegister_Successful() throws Exception {
//        // Arrange
//        RegisterResponse response = new RegisterResponse(UUID.randomUUID(), "accessToken", "refreshToken");
//        when(authService.register(any(RegisterRequest.class))).thenReturn(response);
//
//        // Act & Assert
//        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/register")
//                                              .contentType(MediaType.APPLICATION_JSON)
//                                              .content("{" +
//                                                               "\"email\":\"test@example.com\"," +
//                                                               "\"password\":\"password\"" +
//                                                               "}"
//                                              )
//               )
//               .andExpect(MockMvcResultMatchers.status().isCreated())
//               .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("accessToken"))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value("refreshToken"));
//
//    }
//
//    @Test
//    public void testRegister_BadRequest() throws Exception {
//
//        // Act & Assert
//        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/register")
//                                              .contentType(MediaType.APPLICATION_JSON)
//                                              .content("{" +
//                                                               "\"identity\":\"testcom\"" +
//                                                               "}"
//                                              )
//               )
//               .andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//    }
//
//    @Test
//    public void testAuthenticate_Successful() throws Exception {
//        // Arrange
//        AuthenticationResponse response = new AuthenticationResponse("accessToken", "refreshToken");
//
//        when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);
//
//        // Act & Assert
//        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/access_token")
//                                              .contentType(MediaType.APPLICATION_JSON)
//                                              .content("{" +
//                                                               "\"email\":\"test@example.com\"," +
//                                                               "\"password\":\"password\"}"
//                                              )
//               )
//               .andExpect(MockMvcResultMatchers.status().isOk())
//               .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("accessToken")).andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value("refreshToken"))
//               .andDo(print());
//
//    }
//
//    @Test
//    public void testAuthenticate_BadCredentials() throws Exception {
//        // Arrange
//
////        when(authService.authenticate(any(AuthenticationRequest.class))).thenThrow(new UnauthorizedException("Bad credentials"));
//
//        // Act & Assert
//        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/access_token")
//                                              .contentType(MediaType.APPLICATION_JSON)
//                                              .content("{" +
//                                                               "\"email\":\"test@example.com\"," +
//                                                               "\"password\":\"password\"}"
//                                              )
//               )
//               .andExpect(MockMvcResultMatchers.status().isUnauthorized())
//               .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Bad credentials"))
//               .andDo(print());
//
//    }
//
//    @Test
//    public void testRefreshToken_Successful() throws Exception {
//        // Arrange
//        AuthenticationResponse response = new AuthenticationResponse("accessToken", "refreshToken");
//        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);
//
//        // Act & Assert
//        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/refresh_token")
//                                              .contentType(MediaType.APPLICATION_JSON)
//                                              .content("{\"token\":\"refresh_token\"}"
//                                              )
//               )
//               .andExpect(MockMvcResultMatchers.status().isOk())
//               .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("accessToken"))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value("refreshToken"))
//               .andDo(print());
//    }
//
//
//    @Test
//    public void testValidateToken_Successful() throws Exception {
//        // Arrange
//        ValidationResponse response = ValidationResponseMother.authorized();
//
//        when(authService.validateToken(any(), any(List.class))).thenReturn(response);
//
//        // Act & Assert
//        assertValidationTokenBasedOnResponse(response);
//    }
//
//    @Test
//    public void testValidateToken_Unsuccessful() throws Exception {
//        // Arrange
//        ValidationResponse response = ValidationResponseMother.unAuthorized();
//
//        when(authService.validateToken(any(), any(List.class))).thenReturn(response);
//
//        // Act & Assert
//        assertValidationTokenBasedOnResponse(response);
//    }
//
//
//    private void assertValidationTokenBasedOnResponse(ValidationResponse response) throws Exception {
//        // Act & Assert
//        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/validate_token")
//                                              .requestAttr("X-USER-ID", UUID.randomUUID())
//                                              .requestAttr("X-AUTHORITIES", List.of())
//                                              .header("Authorization", "Bearer: valid_token")
//               )
//               .andExpect(MockMvcResultMatchers.status().isOk())
//               .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(response.userId() != null ? response.userId().toString() : null))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.authorities").value(response.authorities()))
//               .andDo(print());
//    }
//
//
//}
