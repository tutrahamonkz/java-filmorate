package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j // Аннотация для автоматической генерации логгера
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Устанавливаем статус 500 (Внутренняя ошибка сервера)
public class InternalServerException extends RuntimeException {
  public InternalServerException(String message) {
    super(message);
    log.warn(message); // Логируем сообщение об ошибке на уровне WARN
  }
}