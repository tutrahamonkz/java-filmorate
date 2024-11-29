package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final LikeDbStorage likeDbStorage;
    private final FilmService filmService;

    private final Map<Long, Map<Long, Double>> diff = new HashMap<>();
    private final Map<Long, Map<Long, Integer>> freq = new HashMap<>();
    private final Map<Long, Double> uPred = new HashMap<>();
    private final Map<Long, Integer> uFreq = new HashMap<>();
    private Map<Long, HashMap<Long, Integer>> data;


    public List<FilmDto> getRecommendFilms(Long userId) {
        clearData();
        data = loadData();

        if (!data.containsKey(userId)) { // Проверка наличия лайков у пользователя
            return new ArrayList<>();
        }

        computeDifferences();
        averageDifferences();

        boolean hasSimilarLikes = predictRatings(userId);

        if (!hasSimilarLikes) { // Проверка наличия похожих лайков у других пользователей
            return new ArrayList<>();
        }

        return buildRecommendations();
    }

    // Метод загружает данные о лайках пользователей из базы данных и создает структуру для хранения этих данных.
    private Map<Long, HashMap<Long, Integer>> loadData() {
        List<Like> likes = likeDbStorage.getAllLikes(); // Получаем все лайки из базы данных
        Map<Long, HashMap<Long, Integer>> data = new HashMap<>();
        for (Like like : likes) {
            // Если в data нет пользователя, добавляем его и создаем для него новую HashMap
            data.computeIfAbsent(like.getUserId(), k -> new HashMap<>())
                    .put(like.getFilmId(), 1); // Добавляем значение 1 если пользователь ставил лайк фильму
        }
        return data;
    }

    // Метод вычисляет разницу лайков между фильмами и частоту совместных лайков для каждой пары фильмов.
    private void computeDifferences() {
        // Проходим по всем лайкам пользователей
        for (Map<Long, Integer> userLike : data.values()) {
            // Проходим по каждой паре фильмов, которым пользователь поставил лайки
            for (Map.Entry<Long, Integer> entry1 : userLike.entrySet()) {
                Long film1 = entry1.getKey();
                Integer value1 = entry1.getValue();
                for (Map.Entry<Long, Integer> entry2 : userLike.entrySet()) {
                    Long film2 = entry2.getKey();
                    Integer value2 = entry2.getValue();
                    // Вычисляем разницу между лайками и заполняем карту diff
                    diff.computeIfAbsent(film1, k -> new HashMap<>())
                            .merge(film2, (double) value1 - value2, Double::sum);
                    // Заполняем карту частот freq
                    freq.computeIfAbsent(film1, k -> new HashMap<>())
                            .merge(film2, 1, Integer::sum);
                }
            }
        }
    }

    // Метод вычисляет средние значения разницы лайков между фильмами на основе частоты совместных лайков.
    private void averageDifferences() {
        for (Long j : diff.keySet()) {
            for (Long i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i);
                int count = freq.get(j).get(i);
                // Рассчитываем среднюю разницу лайков и обновляем значение в карте diff
                diff.get(j).put(i, oldValue / count);
            }
        }
    }

    /* Метод прогнозирует оценки фильмов для пользователя на основе данных о лайках и разницы лайков
    с другими пользователями. */
    private boolean predictRatings(Long userId) {
        Map<Long, Integer> userRatings = data.get(userId);
        Set<Long> userFilmIds = userRatings.keySet();
        boolean hasSimilarLikes = false;
        for (Map.Entry<Long, Integer> entry : userRatings.entrySet()) {
            Long film = entry.getKey();
            Integer rating = entry.getValue();
            for (Map.Entry<Long, Double> entry2 : diff.get(film).entrySet()) {
                Long film2 = entry2.getKey();
                Double diffValue = entry2.getValue();
                // Проверяем, не оценивал ли пользователь уже этот фильм
                if (!userFilmIds.contains(film2)) {
                    // Обновляем прогнозируемую оценку для второго фильма
                    uPred.merge(film2, diffValue + rating, Double::sum);
                    // Обновляем частоту прогнозируемых оценок для второго фильма
                    uFreq.merge(film2, freq.get(film).get(film2), Integer::sum);
                    hasSimilarLikes = true;
                }
            }
        }
        return hasSimilarLikes;
    }

    // Метод формирует список рекомендованных фильмов на основе прогнозируемых оценок.
    private List<FilmDto> buildRecommendations() {
        // Вычисляем средние прогнозируемые оценки для каждого фильма
        Map<Long, Double> results = uPred.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / uFreq.get(e.getKey())));

        // Формируем и возвращаем отсортированный список рекомендованных фильмов
        return results.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .map(filmService::getFilmById)
                .map(FilmMapper::toFilmDto)
                .map(filmService::addGenresToFilmDto)
                .toList();
    }

    // Метод для очистки данных перед расчетом рекомендуемых фильмов
    private void clearData() {
        diff.clear();
        freq.clear();
        uPred.clear();
        uFreq.clear();
    }
}