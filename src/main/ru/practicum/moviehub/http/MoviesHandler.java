package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.api.ErrorResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class MoviesHandler extends BaseHttpHandler {
    private List<Movie> movies = new ArrayList<>();
    private int nextID = 0;

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        String query = ex.getRequestURI().getQuery();
        Gson gson = new Gson();
        if  ("GET".equals(ex.getRequestMethod())) {
            if (path.contains("movies/") && query == null) {
                String[] line = path.split("/");
                try {
                    int id = Integer.parseInt(line[2]);
                    ex.getResponseHeaders().set("Content-Type",CT_JSON);
                    try {
                        String movieStr = gson.toJson(movies.get(id));
                        sendJson(ex,200,movieStr);
                    } catch (RuntimeException e) {
                        ErrorResponse.errorResponse(ex,404,"Фильм не найден");
                    }
                } catch (NumberFormatException e) {
                    ErrorResponse.errorResponse(ex,400,"Некорректный ID");
                }
            } else if (query != null && query.startsWith("year=")) {
                int endFirstYear = query.indexOf('=');
                String yearStr = query.substring(endFirstYear + 1);
                try {
                    int year = Integer.parseInt(yearStr);
                    try {
                        String moviesStr = gson.toJson(movies.stream()
                                                        .filter(movies -> movies.getYear() == year)
                                                        .collect(Collectors.toList()));
                        sendJson(ex,200,moviesStr);
                    } catch (RuntimeException e) {
                        ErrorResponse.errorResponse(ex,404,"Фильм не найден");
                    }
                } catch (NumberFormatException e) {
                    ErrorResponse.errorResponse(ex,400,"Некорректный параметр запроса — 'year'");
                }
            } else {
                if (ex.getResponseBody() == null) {
                    sendNoContent(ex);
                } else {
                    String moviesStr = gson.toJson(movies);
                    sendJson(ex,200,moviesStr);
                }
            }
        } else if ("POST".equals(ex.getRequestMethod())) {
            Headers requestHeaders = ex.getRequestHeaders();
            List<String> contentTypeValues = requestHeaders.get("Content-type");
            if ((contentTypeValues == null) && !(contentTypeValues.contains("application/json"))) {
                ErrorResponse.errorResponse(ex,415,"Неподдерживаемый тип данных");
                return;
            }
            String body = new String(ex.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);
            Movie movie = gson.fromJson(body,Movie.class);
            if ("".equals(movie.getName())) {
                ErrorResponse.errorResponse(ex,422,"Неверное значение поля " + "name");
                return;
            } else if (movie.getName() == null) {
                ErrorResponse.errorResponse(ex,422,"Неверное значение поля " + "name");
                return;
            } else if (movie.getName().length() > 100) {
                ErrorResponse.errorResponse(ex,422,"Слишком длинное название фильма");
                return;
            } else if (1888 > movie.getYear() || 2016 < movie.getYear()) {
                ErrorResponse.errorResponse(ex,422,"Год должен быть между 1888 и 2016");
                return;
            }
            movie.setId(nextID++);
            movies.add(movie);
            String moviesStr = gson.toJson(movies);
            sendJson(ex,201,moviesStr);
        } else if ("DELETE".equals(ex.getRequestMethod())) {
            String[] line = path.split("/");
            try {
                int id = Integer.parseInt(line[2]);
                try {
                    movies.remove(id);
                    sendNoContent(ex);
                } catch (NoSuchElementException e) {
                    ErrorResponse.errorResponse(ex,404,"Фильм не найден");
                }
            } catch (RuntimeException e) {
            ErrorResponse.errorResponse(ex,404,"Фильм не найден");
            }
        }
    }
}
