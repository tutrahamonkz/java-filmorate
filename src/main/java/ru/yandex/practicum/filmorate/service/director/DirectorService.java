package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDBStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
    private final DirectorDBStorage directorDBStorage;


    public DirectorDto createDirector(Director director) { //создание режиссера
        if (director.getName().matches("^\s*$")) {
            log.error("Имя директора должно быть указано");
            throw new BadRequestException("Имя директора должно быть указано");
        }
        directorDBStorage.createDirector(director);
        return DirectorMapper.todirectorDto(director);
    }

    public List<DirectorDto> getDirectors() { //список всех режиссеров
        return directorDBStorage.getDirectors().stream()
                .map(DirectorMapper::todirectorDto)
                .toList();
    }

    public DirectorDto getDirectorById(long id) { //поиск режиссера по id
        checkDirectorById(id);
        return DirectorMapper.todirectorDto(directorDBStorage.getDirectorById(id).get());

    }

    public DirectorDto updateDirector(Director director) { //обновление режиссера
        checkDirectorById(director.getId());
        directorDBStorage.updateDirector(director);
        return DirectorMapper.todirectorDto(directorDBStorage.getDirectorById(director.getId()).get()); //вытаскиваем из базы обновленного режиссера
    }

    public void checkDirectorById(long id) { //проверка наличия режиссера в базе
        if (directorDBStorage.getDirectorById(id).isEmpty()) {
            log.error("Пользователь ввел неверный id");
            throw new NotFoundException("Неверный id режиссера");
        }
    }

    public void deleteDirector(long id) { //удаление директора
        checkDirectorById(id);
        directorDBStorage.deleteDirector(id);
    }

}
