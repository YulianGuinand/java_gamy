package gamy.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gamy.models.Game;
import gamy.models.GameMode;
import gamy.models.Genre;
import javafx.application.Platform;
import java.time.LocalDate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import gamy.models.GamePlatform;

public class RawgApiService {

    private static final String API_KEY = "8d323d8e314e468f9acfc5f986a0a1b2";
    private static final String BASE_URL = "https://api.rawg.io/api/games";
    
    private final HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void searchGamesDetailedAsync(String query, Consumer<List<Game>> onSuccess, Consumer<Throwable> onError) {
        String urlString = String.format("%s?key=%s&search=%s", BASE_URL, API_KEY, query.replace(" ", "+"));

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseDetailedGames)
                .thenAccept(games -> Platform.runLater(() -> onSuccess.accept(games)))
                .exceptionally(ex -> {
                    Platform.runLater(() -> onError.accept(ex));
                    return null;
                });
    }

    private List<Game> parseDetailedGames(String responseBody) {
        List<Game> games = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode results = root.get("results");
            if (results != null && results.isArray()) {
                for (JsonNode node : results) {
                    Game game = new Game();
                    game.setApiId(node.has("id") ? node.get("id").asLong() : null);
                    game.setTitle(node.has("name") ? node.get("name").asText() : "Inconnu");
                    game.setImageUrl(node.has("background_image") ? node.get("background_image").asText() : null);
                    
                    if (node.has("released") && !node.get("released").isNull()) {
                        try {
                            game.setReleaseDate(LocalDate.parse(node.get("released").asText()));
                        } catch (Exception ignored) {}
                    }

                    
                    if (node.has("platforms") && node.get("platforms").isArray()) {
                        Set<GamePlatform> platforms = new HashSet<>();
                        for (JsonNode pNode : node.get("platforms")) {
                            JsonNode platformObj = pNode.get("platform");
                            if (platformObj != null && platformObj.has("name")) {
                                platforms.add(new GamePlatform(platformObj.get("name").asText()));
                            }
                        }
                        game.setPlatforms(platforms);
                    }

                    if (node.has("genres") && node.get("genres").isArray()) {
                        Set<Genre> genres = new HashSet<>();
                        for (JsonNode gNode : node.get("genres")) {
                            if (gNode.has("name")) {
                                genres.add(new Genre(gNode.get("name").asText()));
                            }
                        }
                        game.setGenres(genres);
                    }

                    game.setGameMode(GameMode.BOTH);
                    games.add(game);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return games;
    }
}