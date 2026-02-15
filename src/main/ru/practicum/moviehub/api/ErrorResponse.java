package ru.practicum.moviehub.api;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ErrorResponse {
    protected static final String CT_JSON = "application/json; charset=UTF-8";

    public static void errorResponse(HttpExchange ex, int status,String text) throws IOException {
        ex.getResponseHeaders().set("Content-Type",CT_JSON);
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status,bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}