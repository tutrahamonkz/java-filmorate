package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice // Аннотация, указывающая, что класс будет обрабатывать исключения в REST-контроллерах
public class ErrorHandler {

    @ExceptionHandler // Указывает, что этот метод будет вызываться при возникновении MethodArgumentNotValidException
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Устанавливает статус ответа 400 (BAD REQUEST)
    // Метод для обработки исключений валидации аргументов методов
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        // Разделение сообщения об ошибке для извлечения причин невалидности
        String[] messageArray = e.getMessage().split("default message \\[");
        // Создание объекта StringBuilder для формирования сообщения об ошибке
        StringBuilder message = new StringBuilder();
        for (int i = 2; i < messageArray.length; i++) { // Начинаем с 2, чтобы пропустить первые два элемента
            message.append(messageArray[i].split("]")[0]); // Извлечение сообщения об ошибке
            message.append(". "); // Добавление точки после каждого сообщения
        }
        return new ErrorResponse(message.toString()); // Возвращение сформированного сообщения об ошибке
    }

    @ExceptionHandler // Указывает, что этот метод будет вызываться при возникновении NotFoundException
    @ResponseStatus(HttpStatus.NOT_FOUND) // Устанавливает статус ответа 404 (NOT FOUND)
    // Метод для обработки исключений NotFoundException
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse(e.getMessage()); // Возвращает сообщение об ошибке из исключения
    }

    @ExceptionHandler // Указывает, что этот метод будет вызываться при возникновении InternalServerException
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Устанавливает статус ответа 500 (INTERNAL SERVER ERROR)
    // Метод для обработки исключений InternalServerException
    public ErrorResponse handleInternalServerException(final InternalServerException e) {
        return new ErrorResponse(e.getMessage()); // Возвращает сообщение об ошибке из исключения
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ErrorResponse handleBadRequest(BadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

}