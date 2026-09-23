package gamy.services;

import gamy.daos.GameDAO;
import gamy.daos.LibraryDAO;
import gamy.daos.UserDAO;
import gamy.models.Game;
import gamy.models.GameMode;
import gamy.models.LibraryEntry;
import gamy.models.LibraryStatus;
import gamy.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceTest {

    private LibraryService libraryService;
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private LibraryDAO libraryDAO;
    
    private User testUser;
    private Game testGame;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
        gameDAO = new GameDAO();
        libraryDAO = new LibraryDAO();
        libraryService = new LibraryService(libraryDAO, userDAO, gameDAO);

        long time = System.currentTimeMillis();
        testUser = new User("libservice_" + time + "@gamy.com", "pass", "LibServiceUser_" + time);
        userDAO.create(testUser);

        testGame = new Game("Service Test Game " + time, GameMode.SOLO);
        gameDAO.create(testGame);
    }

    @AfterEach
    public void tearDown() {
        List<LibraryEntry> entries = libraryDAO.findByUserId(testUser.getId());
        for (LibraryEntry entry : entries) {
            libraryDAO.delete(entry);
        }
        gameDAO.delete(testGame);
        userDAO.delete(testUser);
    }

    @Test
    public void testAddAndRemoveGame() throws Exception {
        // ADD
        LibraryEntry entry = libraryService.addGameToLibrary(testUser.getId(), testGame.getId(), LibraryStatus.WISHLIST);
        assertNotNull(entry.getId(), "L'entree doit etre creee.");
        assertEquals(LibraryStatus.WISHLIST, entry.getStatus());

        // DOUBLE TEST
        Exception duplicateException = assertThrows(Exception.class, () -> {
            libraryService.addGameToLibrary(testUser.getId(), testGame.getId(), LibraryStatus.OWNED);
        });
        assertEquals("Ce jeu est deja dans votre bibliotheque.", duplicateException.getMessage());

        // DELETE
        libraryService.removeGameFromLibrary(entry.getId());
        assertNull(libraryDAO.read(entry.getId()), "L'entree doit etre supprimee de la base de donnees.");
    }

    @Test
    public void testUpdateGameStatus() throws Exception {
        LibraryEntry entry = libraryService.addGameToLibrary(testUser.getId(), testGame.getId(), LibraryStatus.OWNED);
        
        libraryService.updateGameStatus(entry.getId(), LibraryStatus.FAVORITE);
        
        LibraryEntry updatedEntry = libraryDAO.read(entry.getId());
        assertEquals(LibraryStatus.FAVORITE, updatedEntry.getStatus(), "Le statut doit etre mis a jour.");
    }
}