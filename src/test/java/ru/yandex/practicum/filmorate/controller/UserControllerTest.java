package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest() // Аннотация для указания, что это тестовый класс, который будет загружать контекст Spring.
// Автоматическая конфигурация MockMvc для тестирования контроллеров без необходимости запуска сервера.
@AutoConfigureMockMvc
// Указывает порядок выполнения тестов на основе аннотаций @Order.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {
    @Autowired // Внедрение зависимости MockMvc для выполнения HTTP-запросов в тестах.
    private MockMvc mockMvc;
    private static ObjectMapper mapper; // Объект для сериализации и десериализации JSON.

    @BeforeAll
    static void setUp() {
        mapper = new ObjectMapper(); // Инициализация ObjectMapper для работы с JSON.
        mapper.registerModule(new JavaTimeModule()); // Регистрация модуля для поддержки Java 8 времени
    }

    @Test
    @Order(1)
    void shouldReturnEmptyList() throws Exception {
        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void shouldReturnInvalidRequestWhenUserIsEmpty() throws Exception {
        this.mockMvc.perform(post("/users"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenUserLoginContainsSpace() throws Exception {
        User user = User.builder()
                .login("test test")
                .email("test@test.com")
                .birthday(LocalDate.parse("2000-08-20"))
                .build();
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenUserLoginIsNull() throws Exception {
        User user = User.builder()
                .name("test")
                .email("test@test.com")
                .birthday(LocalDate.parse("2000-08-20"))
                .build();
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenUserEmailIsNull() throws Exception {
        User user = User.builder()
                .login("test")
                .birthday(LocalDate.parse("2000-08-20"))
                .build();
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenUserEmailNotContainsSymbolAt() throws Exception {
        User user = User.builder()
                .login("test")
                .email("test.test.com")
                .birthday(LocalDate.parse("2000-08-20"))
                .build();
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenFutureBirthdate() throws Exception {
        User user = User.builder()
                .login("test")
                .email("test@test.com")
                .birthday(LocalDate.parse("9000-08-20"))
                .build();
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenUserIdEmpty() throws Exception {
        User user = User.builder()
                .login("test")
                .email("test@test.com")
                .birthday(LocalDate.parse("2000-08-20"))
                .build();
        this.mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInvalidRequestWhenUserIdInvalid() throws Exception {
        User user = User.builder()
                .id(999L)
                .login("test")
                .email("test@test.com")
                .birthday(LocalDate.parse("2000-08-20"))
                .build();
        this.mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnCorrectRequestWhenCreateUser() throws Exception {
        User user = User.builder()
                .login("test")
                .email("test@test.com")
                .birthday(LocalDate.parse("2000-08-20"))
                .build();
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.login").value(user.getLogin()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.birthday").value(user.getBirthday().toString()));
    }

    @Test
    void shouldReturnCorrectRequestWhenUpdateUser() throws Exception {
        User user = User.builder()
                .login("test")
                .email("test@test.com")
                .build();
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());
        user.setId(1L);
        user.setName("testName");
        this.mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.login").value(user.getLogin()));
    }
}