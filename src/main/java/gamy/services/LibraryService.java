package gamy.services;

import gamy.daos.GameDAO;
import gamy.daos.LibraryDAO;
import gamy.daos.UserDAO;
import gamy.models.Game;
import gamy.models.LibraryEntry;
import gamy.models.LibraryStatus;
import gamy.models.User;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class LibraryService {

    private final LibraryDAO libraryDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public LibraryService() {
        this.libraryDAO = new LibraryDAO();
        this.userDAO = new UserDAO();
        this.gameDAO = new GameDAO();
    }

    public LibraryService(LibraryDAO libraryDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.libraryDAO = libraryDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public LibraryEntry addGameToLibrary(Long userId, Long gameId, LibraryStatus status) throws Exception {
        LibraryEntry existingEntry = libraryDAO.findByUserAndGame(userId, gameId);
        if (existingEntry != null) {
            throw new Exception("Ce jeu est deja dans votre bibliotheque.");
        }

        User user = userDAO.read(userId);
        Game game = gameDAO.read(gameId);

        if (user == null || game == null) {
            throw new Exception("Utilisateur ou jeu introuvable.");
        }

        LibraryEntry newEntry = new LibraryEntry(user, game, status);
        libraryDAO.create(newEntry);
        
        return newEntry;
    }

    public void updateGameStatus(Long entryId, LibraryStatus newStatus) throws Exception {
        LibraryEntry entry = libraryDAO.read(entryId);
        if (entry == null) {
            throw new Exception("Entree de bibliotheque introuvable.");
        }

        entry.setStatus(newStatus);
        libraryDAO.update(entry);
    }

    public void removeGameFromLibrary(Long entryId) throws Exception {
        LibraryEntry entry = libraryDAO.read(entryId);
        if (entry != null) {
            libraryDAO.delete(entry);
        } else {
            throw new Exception("Entree de bibliotheque introuvable.");
        }
    }

    public List<LibraryEntry> getUserLibrary(Long userId) {
        return libraryDAO.findByUserId(userId);
    }

    public void getFriendLibraryAsync(Long friendId, Consumer<List<LibraryEntry>> onSuccess, Consumer<Throwable> onError) {
        CompletableFuture.supplyAsync(() -> libraryDAO.findByUserId(friendId))
            .thenAccept(entries -> Platform.runLater(() -> onSuccess.accept(entries)))
            .exceptionally(ex -> {
                Platform.runLater(() -> onError.accept(ex));
                return null;
            });
    }
}