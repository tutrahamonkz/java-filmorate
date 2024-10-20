package ru.yandex.practicum.filmorate.service.mpa;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

@Service // Аннотация указывает, что данный класс является сервисом и может быть использован в контексте Spring
public class MpaService {
    private final MpaDbStorage mpaDbStorage;  // Хранилище для работы с данными MPA

    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    // Метод для получения списка всех рейтингов MPA
    public List<Mpa> getAllMpa() {
        return mpaDbStorage.getAllMpa(); // Возвращаем все рейтинги из хранилища
    }

    // Метод для получения рейтинга по его идентификатору
    public Mpa getMpaById(@PathVariable Long id) {
        // Пытаемся получить рейтинг по ID. Если не найден, выбрасываем исключение NotFoundException.
        return mpaDbStorage.getMpaById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id: " + id + " не найден"));
    }
}