package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest() // Аннотация для указания, что это тестовый класс, который будет загружать контекст Spring.
// Автоматическая конфигурация MockMvc для тестирования контроллеров без необходимости запуска сервера.
@AutoConfigureMockMvc
// Указывает порядок выполнения тестов на основе аннотаций @Order.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmControllerTest {

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
        this.mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void shouldReturnInvalidRequestWhenFilmIsEmpty() throws Exception {
        this.mockMvc.perform(post("/films"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenFilmNameIsNull() throws Exception {
        Film film = Film.builder()
                .build();
        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenFilmNameIsEmpty() throws Exception {
        Film film = Film.builder()
                .name("")
                .build();
        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenFilmNameIsOnlySpace() throws Exception {
        Film film = Film.builder()
                .name("   ")
                .build();
        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenFilmDescriptionLengthMoreMaximum() throws Exception {
        String description = "1".repeat(Film.MAXIMUM_DESCRIPTION_LENGTH) + "1"; // больше на 1 символ чем максимум
        Film film = Film.builder()
                .name("test")
                .description(description)
                .build();
        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenFilmReleaseDateBeforeMinDate() throws Exception {
        Film film = Film.builder()
                .name("test")
                .releaseDate(LocalDate.parse("1895-12-27"))
                .build();
        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenFilmDurationNegative() throws Exception {
        Film film = Film.builder()
                .name("test")
                .duration(-1)
                .build();
        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInvalidRequestWhenFilmIdEmpty() throws Exception {
        Film film = Film.builder()
                .name("test")
                .description("Test description")
                .releaseDate(LocalDate.parse("2000-01-01"))
                .duration(100)
                .build();
        this.mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInvalidRequestWhenFilmIdInvalid() throws Exception {
        Film film = Film.builder()
                .id(999L)
                .name("test")
                .description("Test description")
                .releaseDate(LocalDate.parse("2000-01-01"))
                .duration(100)
                .build();
        this.mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnCorrectRequestWhenCreateFilm() throws Exception {
        Film film = Film.builder()
                .name("test")
                .description("Test description")
                .releaseDate(LocalDate.parse("2000-01-01"))
                .duration(100)
                .build();
        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(film.getName()))
                .andExpect(jsonPath("$.description").value(film.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(film.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(film.getDuration().toString()));
    }

    @Test
    void shouldReturnCorrectRequestWhenUpdateFilm() throws Exception {
        Film film = Film.builder()
                .name("test")
                .description("Test description")
                .releaseDate(LocalDate.parse("2000-01-01"))
                .duration(100)
                .build();
        film.setId(1L);
        film.setName("newTest");
        film.setDescription("");
        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(film.getName()))
                .andExpect(jsonPath("$.description").value(film.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(film.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(film.getDuration().toString()));
    }
}