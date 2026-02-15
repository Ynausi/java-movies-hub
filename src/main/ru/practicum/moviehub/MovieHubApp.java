package ru.practicum.moviehub;

import ru.practicum.moviehub.http.MoviesHandler;
import ru.practicum.moviehub.http.MoviesServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class MovieHubApp {
    private static final String BASE = "http://localhost:8080";

    public static void main(String[] args) {
        MoviesServer server = new MoviesServer();
        //Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        //Я всё проверял через Insomnia, не знаю нужно ли тут писать запросы
        server.createEndPoint("/movies",new MoviesHandler());
        server.start();

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .GET()
                .build();
        HttpRequest req2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/movies"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"asg\", \"year\": \"1784\"}"))
                .build();
        HttpRequest req3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/movies/1"))
                .GET()
                .build();
        HttpRequest req4 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/movies?year=1984"))
                .GET()
                .build();
        HttpResponse<String> resp;
        try {
             resp =
                    client.send(req2, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            resp =
                    client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            resp =
                    client.send(req3, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            resp =
                    client.send(req4, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        }  catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        server.stop();
    }
}