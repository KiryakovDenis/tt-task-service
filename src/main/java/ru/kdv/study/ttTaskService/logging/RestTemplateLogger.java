package ru.kdv.study.ttTaskService.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

@Slf4j
public class RestTemplateLogger implements ClientHttpRequestInterceptor {

    private static final String LOGGING_ERROR_MESSAGE = "Ошибка логирования исходящего запроса";
    private static final String EMPTY_VALUE = "<<EMPTY>>";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        long startTime = System.currentTimeMillis();
        String responseData = null;
        ClientHttpResponse response = null;
        try {
            response = execution.execute(request, body);
            responseData = handleResponseData(response);

        } catch (Exception e) {
            responseData = handleResponseData(e);

        } finally {
            long stopTime = System.currentTimeMillis();
            String message = new StringJoiner("\n")
                    .add(String.format("Исходящий запрос [длительность: %d]", (stopTime - startTime)))
                    .add(getRequestLog(request, body))
                    .add(responseData)
                    .toString();

            log.info(message);
        }

        return response;
    }

    private String handleResponseData(ClientHttpResponse response) throws Exception {
        try {
            return getResponseData(response);
        } catch (Exception e) {
            log.warn(LOGGING_ERROR_MESSAGE, e);
            throw e;
        }
    }

    private String handleResponseData(Exception e) {
        try {
            return getResponseErrorData(e);
        } catch (Exception exception) {
            log.warn(LOGGING_ERROR_MESSAGE, exception);
            throw exception;
        }
    }

    private String getResponseData(ClientHttpResponse response) throws IOException {


        return new StringJoiner("\n")
                .add(String.format("HTTP status: %s", response.getStatusCode()))
                .add(String.format("Headers: %s", (!response.getHeaders().isEmpty() ? response.getHeaders() : EMPTY_VALUE)))
                .add(String.format("Payload: %s", (!response.getBody().toString().isEmpty() ? response.getBody().toString(): EMPTY_VALUE)))
                .toString();
    }

    private String getResponseErrorData(Exception e) {
        return new StringJoiner("\n")
                .add(String.format("HTTP Status: %s", "Error"))
                .add(String.format("Error: %s", e.getMessage()))
                .toString();
    }

    private String getRequestLog(HttpRequest request, byte[] body) {
        String payload = new String(body, StandardCharsets.UTF_8);
        return new StringJoiner("\n")
                .add(String.format("Method: %s", request.getMethod()))
                .add(String.format("Headers: %s", (!request.getHeaders().isEmpty() ? request.getHeaders() : EMPTY_VALUE)))
                .add(String.format("Payload: %s", (!payload.isEmpty() ? payload : EMPTY_VALUE)))
                .toString();
    }
}