package gamy.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RawgApiService {

    private static final String API_KEY = "8d323d8e314e468f9acfc5f986a0a1b2";
    private static final String BASE_URL = "https://api.rawg.io/api/games";
    
    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void searchGamesAsync(String query, Consumer<List<String>> onSuccess, Consumer<Throwable> onError) {
        String urlString = String.format("%s?key=%s&search=%s", BASE_URL, API_KEY, query.replace(" ", "+"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseGameTitles)
                .thenAccept(games -> Platform.runLater(() -> onSuccess.accept(games)))
                .exceptionally(ex -> {
                    Platform.runLater(() -> onError.accept(ex));
                    return null;
                });
    }

    private List<String> parseGameTitles(String responseBody) {
        List<String> titles = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode results = root.get("results");
            if (results != null && results.isArray()) {
                for (JsonNode node : results) {
                    titles.add(node.get("name").asText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return titles;
    }
}