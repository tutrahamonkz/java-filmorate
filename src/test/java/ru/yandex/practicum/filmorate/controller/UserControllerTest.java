package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test") // Аннотация для указания, что это тестовый класс, который будет загружать контекст Spring.
// Автоматическая конфигурация MockMvc для тестирования контроллеров без необходимости запуска сервера.
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired // Внедрение зависимости MockMvc для выполнения HTTP-запросов в тестах.
    private MockMvc mockMvc;

    private static ObjectMapper mapper; // Объект для сериализации и десериализации JSON.

    @Autowired // Внедрение сервиса пользователя для использования в тестах.
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeAll
    static void setUp() {
        mapper = new ObjectMapper(); // Инициализация ObjectMapper для работы с JSON.
        mapper.registerModule(new JavaTimeModule()); // Регистрация модуля для поддержки Java 8 времени
        // Установка формата даты для сериализации/десериализации.
        mapper.setDateFormat(new SimpleDateFormat("yyyy-M-dd"));
    }

    @BeforeEach
    void clearTable() {
        JdbcTestUtils.deleteFromTables(jdbc, "likes");
        int countLike = JdbcTestUtils.countRowsInTable(jdbc, "likes");
        System.out.println("Количество записей в таблице likes после очистки: " + countLike);
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
        String users = mapper.writeValueAsString(userService.getUsers());
        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(users));
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
                .birthday(LocalDate.parse("2000-08-20"))
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

    @Test
    void shouldReturnCorrectRequestWhenAddFriend() throws Exception {
        this.mockMvc.perform(delete("/users/1/friends/2"));
        createTwoUsers();
        this.mockMvc.perform(put("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString("")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.friends").isNotEmpty());
    }

    @Test
    void shouldReturnInvalidRequestWhenAddFriendUnknownId() throws Exception {
        createTwoUsers();
        this.mockMvc.perform(put("/users/1/friends/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString("")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnCorrectRequestWhenDeleteFriend() throws Exception {
        this.mockMvc.perform(delete("/users/1/friends/2"));
        createTwoUsers();
        this.mockMvc.perform(put("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString("")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.friends").isNotEmpty());
        this.mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.friends").isEmpty());
    }

    @Test
    void shouldReturnInvalidRequestWhenDeleteFriendUnknownId() throws Exception {
        createTwoUsers();
        this.mockMvc.perform(delete("/users/1/friends/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Пользователь с id: 999 не найден"));
    }

    @Test
    void shouldReturnInvalidRequestWhenDeleteUnknownId() throws Exception {
        createTwoUsers();
        this.mockMvc.perform(delete("/users/999/friends/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Пользователь с id: 999 не найден"));
    }

    @Test
    void shouldReturnCorrectRequestWhenGetFriend() throws Exception {
        this.mockMvc.perform(delete("/users/1/friends/2"));
        createTwoUsers();
        this.mockMvc.perform(put("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString("")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.friends").isNotEmpty());
        this.mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value(2))
                .andExpect(jsonPath("[0].friends").isEmpty());
    }

    @Test
    void shouldReturnInvalidRequestWhenGetFriendUnknownId() throws Exception {
        createTwoUsers();
        this.mockMvc.perform(put("/users/1/friends/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString("")))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Пользователь с id: 999 не найден"));
    }

    @Test
    void shouldReturnCorrectRequestWhenGetCommonFriend() throws Exception {
        createTwoUsers();
        User user = User.builder()
                .login("test")
                .email("test@test.com")
                .birthday(LocalDate.parse("2000-08-20"))
                .build();
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/users/2/friends/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString("")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.friends").isNotEmpty());
        this.mockMvc.perform(put("/users/3/friends/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString("")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.friends").isNotEmpty());
        this.mockMvc.perform(get("/users/2/friends/common/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].friends").isArray());
    }

    void createTwoUsers() throws Exception {
        User user = User.builder()
                .login("test")
                .email("test@test.com")
                .birthday(LocalDate.parse("2000-08-20"))
                .build();
        User user2 = User.builder()
                .login("test2")
                .email("test2@test.com")
                .birthday(LocalDate.parse("2000-08-20"))
                .build();
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user2)))
                .andExpect(status().isOk());
    }
}