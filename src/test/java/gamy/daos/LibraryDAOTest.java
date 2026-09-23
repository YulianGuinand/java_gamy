package gamy.daos;

import gamy.models.Game;
import gamy.models.GameMode;
import gamy.models.LibraryEntry;
import gamy.models.LibraryStatus;
import gamy.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryDAOTest {

    private static LibraryDAO libraryDAO;
    private static UserDAO userDAO;
    private static GameDAO gameDAO;

    @BeforeAll
    public static void setUp() {
        libraryDAO = new LibraryDAO();
        userDAO = new UserDAO();
        gameDAO = new GameDAO();
    }

    @Test
    public void testLibraryOperations() {
        // CREATE
        User user = new User("lib_test@gamy.com", "pass", "LibTester");
        userDAO.create(user);
        
        Game game = new Game("Super Mario", GameMode.MULTI);
        gameDAO.create(game);

        //  CREATE LIBRARY
        LibraryEntry entry = new LibraryEntry(user, game, LibraryStatus.OWNED);
        libraryDAO.create(entry);
        assertNotNull(entry.getId(), "L'ID de l'entree doit etre genere.");

        // READ
        List<LibraryEntry> userLibrary = libraryDAO.findByUserId(user.getId());
        assertEquals(1, userLibrary.size(), "L'utilisateur doit avoir 1 jeu dans sa bibliotheque.");
        assertEquals("Super Mario", userLibrary.get(0).getGame().getTitle());
        assertEquals(LibraryStatus.OWNED, userLibrary.get(0).getStatus());

        // EDIT AND UPDATE
        entry.setStatus(LibraryStatus.FAVORITE);
        libraryDAO.update(entry);
        
        LibraryEntry updatedEntry = libraryDAO.read(entry.getId());
        assertEquals(LibraryStatus.FAVORITE, updatedEntry.getStatus());

        // DELETE
        libraryDAO.delete(updatedEntry);
        assertNull(libraryDAO.read(entry.getId()));
        
        gameDAO.delete(game);
        userDAO.delete(user);
    }
}