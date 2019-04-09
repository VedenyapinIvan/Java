package ru.via.decode.service;

public interface Answerable<Rs, T> {

    /**
     * Формирование ответа с ошибкой
     *
     * @param statusCode - код ошибки
     * @param severity   - серьезность ошибки
     * @param statusDesc - описание ошибки
     * @return возвращает ответ с ошибкой
     */
    Rs createErrorAnswer(Integer statusCode, String severity, String statusDesc);

    /**
     * Формирование ответа успешного
     *
     * @param value - декодированное значение
     * @return возвращает ответ успешный
     */
    Rs createSuccessAnswer(T value);
}
