package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Аннотация для указания, что это тестовый класс, который будет загружать контекст Spring.
@SpringBootTest(properties = "spring.profiles.active=test")
// Автоматическая конфигурация MockMvc для тестирования контроллеров без необходимости запуска сервера.
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired // Внедрение зависимости MockMvc для выполнения HTTP-запросов в тестах.
    private MockMvc mockMvc;

    private static ObjectMapper mapper; // Объект для сериализации и десериализации JSON.

    @Autowired // Внедрение сервиса фильмов для использования в тестах.
    private FilmService filmService;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeAll
    static void setUp() {
        mapper = new ObjectMapper(); // Инициализация ObjectMapper для работы с JSON.
        mapper.registerModule(new JavaTimeModule()); // Регистрация модуля для поддержки Java 8 времени
        // Установка формата даты для сериализации/десериализации.
        mapper.setDateFormat(new SimpleDateFormat("yyyy-M-dd"));
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
        String films = mapper.writeValueAsString(filmService.getFilms());
        this.mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().string(films));
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
                .andExpect(status().isInternalServerError());
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
        Mpa mpa = Mpa.builder().build();
        Film film = Film.builder()
                .name("test")
                .description("Test description")
                .releaseDate(LocalDate.parse("2000-01-01"))
                .duration(100)
                .mpa(mpa)
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
        Mpa mpa = Mpa.builder().build();
        Film film = Film.builder()
                .name("test")
                .description("Test description")
                .releaseDate(LocalDate.parse("2000-01-01"))
                .duration(100)
                .mpa(mpa)
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

    @Test
    void shouldReturnCorrectRequestWhenAddLike() throws Exception {
        createUser();
        createFilm();
        this.mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.likes").value(1));
    }

    @Test
    void shouldReturnInvalidRequestWhenAddLikeWrongFilmId() throws Exception {
        createUser();
        createFilm();
        this.mockMvc.perform(put("/films/999/like/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInvalidRequestWhenAddLikeWrongUserId() throws Exception {
        createUser();
        createFilm();
        this.mockMvc.perform(put("/films/1/like/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnCurrentRequestWhenDeleteLike() throws Exception {
        createUser();
        createFilm();
        this.mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.likes").isEmpty());
    }

    @Test
    void shouldReturnInvalidRequestWhenDeleteLikeWrongFilmId() throws Exception {
        createUser();
        createFilm();
        this.mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/films/999/like/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInvalidRequestWhenDeleteLikeWrongUserId() throws Exception {
        createUser();
        createFilm();
        this.mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/films/1/like/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInvalidRequestWhenCreateFilmByWrongMpa() throws Exception {
        Mpa mpa = Mpa.builder()
                .id(999L)
                .build();
        Film film = Film.builder()
                .name("test")
                .description("Test description")
                .releaseDate(LocalDate.parse("2000-01-01"))
                .duration(100)
                .mpa(mpa)
                .build();
        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    void createUser() throws Exception {
        User user = User.builder()
                .login("test")
                .email("test@test.com")
                .birthday(LocalDate.parse("2000-08-20"))
                .build();
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    void createFilm() throws Exception {
        Mpa mpa = Mpa.builder()
                .id(1L)
                .build();
        Film film = Film.builder()
                .name("test")
                .description("Test description")
                .releaseDate(LocalDate.parse("2000-01-01"))
                .duration(100)
                .mpa(mpa)
                .build();
        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());
    }
}