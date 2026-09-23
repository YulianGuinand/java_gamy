package gamy.services;

import gamy.daos.GameDAO;
import gamy.daos.LibraryDAO;
import gamy.models.Game;
import gamy.models.GameMode;
import gamy.models.LibraryEntry;
import gamy.models.RandomResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomizerService {

    private final LibraryDAO libraryDAO;
    private final GameDAO gameDAO;
    private final Random random;

    public RandomizerService() {
        this.libraryDAO = new LibraryDAO();
        this.gameDAO = new GameDAO();
        this.random = new Random();
    }

    public RandomizerService(LibraryDAO libraryDAO, GameDAO gameDAO) {
        this.libraryDAO = libraryDAO;
        this.gameDAO = gameDAO;
        this.random = new Random();
    }

    public RandomResult randomizeSolo(Long userId, List<String> genreFilters, List<String> platformFilters, List<Long> excludedGameIds) throws Exception {
        // RETRIEVE USER LIBRARY
        List<LibraryEntry> entries = libraryDAO.findByUserId(userId);
        List<Game> pool = entries.stream()
                .map(entry -> gameDAO.findByIdWithRelations(entry.getGame().getId()))
                .collect(Collectors.toList());

        // FILTER
        pool = applyFilters(pool, genreFilters, platformFilters, excludedGameIds, true);

        if (pool.isEmpty()) {
            throw new Exception("Aucun jeu ne correspond a vos criteres.");
        }

        Game pickedGame = pool.get(random.nextInt(pool.size()));
        return new RandomResult(pickedGame, false, 0.0);
    }

    public RandomResult randomizeMulti(List<Long> participantIds, List<String> genreFilters, List<String> platformFilters, List<Long> excludedGameIds, List<Long> includedGameIds) throws Exception {
        
        if (participantIds == null || participantIds.isEmpty()) {
            throw new Exception("Il faut au moins un participant.");
        }

        List<Long> intersectionIds = new ArrayList<>();
        boolean firstUser = true;

        for (Long userId : participantIds) {
            List<LibraryEntry> entries = libraryDAO.findByUserId(userId);
            List<Long> userGameIds = entries.stream()
                    .map(entry -> entry.getGame().getId())
                    .collect(Collectors.toList());

            if (firstUser) {
                intersectionIds.addAll(userGameIds);
                firstUser = false;
            } else {
                intersectionIds.retainAll(userGameIds);
            }
        }

        List<Game> pool = intersectionIds.stream()
                .map(id -> gameDAO.findByIdWithRelations(id))
                .collect(Collectors.toList());

        final List<Long> baseIntersectionIds = new ArrayList<>(intersectionIds);

        if (includedGameIds != null) {
            for (Long gameId : includedGameIds) {
                boolean alreadyInPool = pool.stream().anyMatch(g -> g.getId().equals(gameId));
                if (!alreadyInPool) {
                    Game includedGame = gameDAO.findByIdWithRelations(gameId);
                    if (includedGame != null) {
                        pool.add(includedGame);
                    }
                }
            }
        }

        pool = applyFilters(pool, genreFilters, platformFilters, excludedGameIds, false);

        if (pool.isEmpty()) {
            throw new Exception("Aucun jeu ne correspond aux criteres du groupe.");
        }

        Game pickedGame = pool.get(random.nextInt(pool.size()));

        boolean isMissingForSomeone = !baseIntersectionIds.contains(pickedGame.getId());
        Double price = (pickedGame.getPrice() != null) ? pickedGame.getPrice() : 0.0;
        
        return new RandomResult(pickedGame, isMissingForSomeone, isMissingForSomeone ? price : 0.0);
    }

    private List<Game> applyFilters(List<Game> pool, List<String> genreFilters, List<String> platformFilters, List<Long> excludedGameIds, boolean isSolo) {
        return pool.stream()
            // Filtre : Exclusions
            .filter(g -> excludedGameIds == null || !excludedGameIds.contains(g.getId()))
            // Filtre : Mode de jeu
            .filter(g -> {
                if (isSolo) {
                    return g.getGameMode() == GameMode.SOLO || g.getGameMode() == GameMode.BOTH;
                } else {
                    return g.getGameMode() == GameMode.MULTI || g.getGameMode() == GameMode.BOTH;
                }
            })
            // Filtre : Genres
            .filter(g -> {
                if (genreFilters == null || genreFilters.isEmpty()) return true;
                return g.getGenres().stream().anyMatch(genre -> genreFilters.contains(genre.getName()));
            })
            // Filtre : Plateformes
            .filter(g -> {
                if (platformFilters == null || platformFilters.isEmpty()) return true;
                return g.getPlatforms().stream().anyMatch(platform -> platformFilters.contains(platform.getName()));
            })
            .collect(Collectors.toList());
    }
}