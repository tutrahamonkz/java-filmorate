package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.List;

@RestController // Аннотация, указывающая, что класс является REST-контроллером
@RequestMapping("/mpa") // Общий путь для всех методов контроллера
public class MpaController {

    private final MpaService mpaService; // Сервис для работы с MPA

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    // Метод для получения списка всех MPA
    @GetMapping
    public List<Mpa> getAllMpa() {
        return mpaService.getAllMpa();
    }

    // Метод для получения MPA по его идентификатору
    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        return mpaService.getMpaById(id);
    }
}
