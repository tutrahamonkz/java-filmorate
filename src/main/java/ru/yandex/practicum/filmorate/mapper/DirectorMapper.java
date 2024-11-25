package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectorMapper {
    public static DirectorDto toDirectorDto(Director director) {
        return DirectorDto.builder()
                .id(director.getId())
                .name(director.getName())
                .build();
    }
}
