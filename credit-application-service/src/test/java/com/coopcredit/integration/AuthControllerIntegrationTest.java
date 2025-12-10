package com.coopcredit.integration;

import com.coopcredit.domain.model.Role;
import com.coopcredit.infrastructure.adapters.input.rest.dto.AuthRequest;
import com.coopcredit.infrastructure.adapters.input.rest.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para AuthController usando MockMvc.
 * Estas pruebas verifican el flujo completo de autenticación incluyendo
 * registro, login y acceso a endpoints protegidos.
 */
@DisplayName("AuthController - Pruebas de Integración")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerIntegrationTest extends AbstractMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String authToken;
    private static final String TEST_USERNAME = "testuser_" + System.currentTimeMillis();
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_EMAIL = "testuser_" + System.currentTimeMillis() + "@test.com";

    @Nested
    @DisplayName("POST /api/auth/register")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class RegisterTests {

        @Test
        @Order(1)
        @DisplayName("Debe registrar un nuevo usuario exitosamente")
        void shouldRegisterNewUserSuccessfully() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    TEST_USERNAME,
                    TEST_PASSWORD,
                    TEST_EMAIL
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                    .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                    .andExpect(jsonPath("$.role").value(Role.AFILIADO.name()))
                    .andExpect(jsonPath("$.id").isNumber());
        }

        @Test
        @Order(2)
        @DisplayName("Debe rechazar registro con usuario duplicado")
        void shouldRejectDuplicateUsername() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    TEST_USERNAME, // mismo usuario
                    "otherpassword",
                    "other@email.com"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Debe rechazar registro con datos inválidos")
        void shouldRejectInvalidRegistrationData() throws Exception {
            // Usuario muy corto
            RegisterRequest request = new RegisterRequest(
                    "ab", // menos de 3 caracteres
                    "pass123",
                    "valid@email.com"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Debe rechazar registro con email inválido")
        void shouldRejectInvalidEmail() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "validuser",
                    "password123",
                    "invalid-email" // email inválido
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Debe rechazar registro con contraseña muy corta")
        void shouldRejectShortPassword() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "newuser",
                    "12345", // menos de 6 caracteres
                    "new@email.com"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class LoginTests {

        @Test
        @Order(1)
        @DisplayName("Debe autenticar usuario existente y devolver token JWT")
        void shouldAuthenticateUserAndReturnToken() throws Exception {
            // Primero registrar el usuario
            String uniqueUsername = "logintest_" + System.currentTimeMillis();
            RegisterRequest registerRequest = new RegisterRequest(
                    uniqueUsername,
                    "password123",
                    uniqueUsername + "@test.com"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated());

            // Luego hacer login
            AuthRequest loginRequest = new AuthRequest(uniqueUsername, "password123");

            MvcResult result = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.username").value(uniqueUsername))
                    .andExpect(jsonPath("$.role").value(Role.AFILIADO.name()))
                    .andReturn();

            // Guardar token para otras pruebas
            String responseContent = result.getResponse().getContentAsString();
            authToken = objectMapper.readTree(responseContent).get("token").asText();
        }

        @Test
        @DisplayName("Debe rechazar credenciales inválidas")
        void shouldRejectInvalidCredentials() throws Exception {
            AuthRequest request = new AuthRequest("nonexistent", "wrongpassword");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError()); // Puede ser 400 o 401
        }

        @Test
        @DisplayName("Debe rechazar contraseña incorrecta")
        void shouldRejectWrongPassword() throws Exception {
            // Primero registrar
            String uniqueUsername = "wrongpass_" + System.currentTimeMillis();
            RegisterRequest registerRequest = new RegisterRequest(
                    uniqueUsername,
                    "correctpassword",
                    uniqueUsername + "@test.com"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated());

            // Intentar login con contraseña incorrecta
            AuthRequest loginRequest = new AuthRequest(uniqueUsername, "wrongpassword");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().is4xxClientError()); // Puede ser 400 o 401
        }
    }

    @Nested
    @DisplayName("GET /api/auth/me")
    class GetCurrentUserTests {

        @Test
        @DisplayName("Debe devolver usuario actual con token válido")
        void shouldReturnCurrentUserWithValidToken() throws Exception {
            // Registrar y hacer login
            String uniqueUsername = "metest_" + System.currentTimeMillis();
            RegisterRequest registerRequest = new RegisterRequest(
                    uniqueUsername,
                    "password123",
                    uniqueUsername + "@test.com"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated());

            AuthRequest loginRequest = new AuthRequest(uniqueUsername, "password123");
            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                    .get("token").asText();

            // Obtener usuario actual
            mockMvc.perform(get("/api/auth/me")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(uniqueUsername))
                    .andExpect(jsonPath("$.role").value(Role.AFILIADO.name()));
        }

        @Test
        @DisplayName("Debe rechazar petición sin token")
        void shouldRejectRequestWithoutToken() throws Exception {
            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().is4xxClientError()); // Puede ser 401 o 403
        }

        @Test
        @DisplayName("Debe rechazar token inválido")
        void shouldRejectInvalidToken() throws Exception {
            mockMvc.perform(get("/api/auth/me")
                            .header("Authorization", "Bearer invalid.token.here"))
                    .andExpect(status().is4xxClientError()); // Puede ser 401 o 403
        }
    }

    @Nested
    @DisplayName("GET /api/auth/users - Admin Only")
    class GetAllUsersTests {

        @Test
        @DisplayName("Debe denegar acceso a usuarios no admin")
        void shouldDenyAccessToNonAdminUsers() throws Exception {
            // Registrar un usuario normal
            String uniqueUsername = "normaluser_" + System.currentTimeMillis();
            RegisterRequest registerRequest = new RegisterRequest(
                    uniqueUsername,
                    "password123",
                    uniqueUsername + "@test.com"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated());

            AuthRequest loginRequest = new AuthRequest(uniqueUsername, "password123");
            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                    .get("token").asText();

            // Intentar acceder a endpoint de admin
            mockMvc.perform(get("/api/auth/users")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isForbidden());
        }
    }
}
