package gamy.services;

import gamy.daos.GameDAO;
import gamy.daos.LibraryDAO;
import gamy.daos.UserDAO;
import gamy.models.*;
import gamy.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RandomizerServiceTest {

    private RandomizerService randomizerService;
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private LibraryDAO libraryDAO;

    private User user1, user2;
    private Game gameSolo, gameMulti, gameExternal;
    private Genre genreRPG, genreFPS;
    private GamePlatform platformPC;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
        gameDAO = new GameDAO();
        libraryDAO = new LibraryDAO();
        randomizerService = new RandomizerService(libraryDAO, gameDAO);

        long time = System.currentTimeMillis();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            genreRPG = new Genre("RPG_" + time);
            genreFPS = new Genre("FPS_" + time);
            platformPC = new GamePlatform("PC_" + time);
            session.persist(genreRPG);
            session.persist(genreFPS);
            session.persist(platformPC);
            tx.commit();
        }

        gameSolo = new Game("The Witcher", GameMode.SOLO);
        gameSolo.getGenres().add(genreRPG);
        gameSolo.getPlatforms().add(platformPC);
        gameSolo.setPrice(29.99);
        gameDAO.create(gameSolo);

        gameMulti = new Game("Left 4 Dead", GameMode.MULTI);
        gameMulti.getGenres().add(genreFPS);
        gameMulti.getPlatforms().add(platformPC);
        gameMulti.setPrice(9.99);
        gameDAO.create(gameMulti);

        gameExternal = new Game("Party Game", GameMode.MULTI);
        gameExternal.setPrice(14.99);
        gameDAO.create(gameExternal);

        user1 = new User("rand1_" + time + "@gamy.com", "pass", "PlayerOne_" + time);
        user2 = new User("rand2_" + time + "@gamy.com", "pass", "PlayerTwo_" + time);
        userDAO.create(user1);
        userDAO.create(user2);

        libraryDAO.create(new LibraryEntry(user1, gameSolo, LibraryStatus.OWNED));
        libraryDAO.create(new LibraryEntry(user1, gameMulti, LibraryStatus.OWNED));
        libraryDAO.create(new LibraryEntry(user2, gameMulti, LibraryStatus.OWNED));
    }

    @AfterEach
    public void tearDown() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM LibraryEntry").executeUpdate();
            session.createMutationQuery("DELETE FROM Game").executeUpdate();
            session.createMutationQuery("DELETE FROM Genre").executeUpdate();
            session.createMutationQuery("DELETE FROM Platform").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    public void testRandomizeSolo_Success() throws Exception {
        RandomResult result = randomizerService.randomizeSolo(
                user1.getId(), 
                Arrays.asList(genreRPG.getName()), 
                null, 
                null
        );

        assertNotNull(result);
        assertEquals(gameSolo.getId(), result.getGame().getId());
        assertFalse(result.isRequiresPurchase());
        assertEquals(0.0, result.getPriceToPay());
    }

    @Test
    public void testRandomizeSolo_NoMatchThrowsException() {
        Exception exception = assertThrows(Exception.class, () -> {
            randomizerService.randomizeSolo(user2.getId(), null, null, null);
        });
        assertEquals("Aucun jeu ne correspond a vos criteres.", exception.getMessage());
    }

    @Test
    public void testRandomizeMulti_IntersectionSuccess() throws Exception {
        RandomResult result = randomizerService.randomizeMulti(
                Arrays.asList(user1.getId(), user2.getId()), 
                null, 
                null, 
                null, 
                null
        );

        assertEquals(gameMulti.getId(), result.getGame().getId());
        assertFalse(result.isRequiresPurchase(), "Le jeu est possede par tous.");
    }

    @Test
    public void testRandomizeMulti_WithInclusionRequiresPurchase() throws Exception {
        RandomResult result = randomizerService.randomizeMulti(
                Arrays.asList(user1.getId(), user2.getId()), 
                null, 
                null, 
                Arrays.asList(gameMulti.getId()), // Exclusion
                Arrays.asList(gameExternal.getId()) // Inclusion
        );

        assertEquals(gameExternal.getId(), result.getGame().getId());
        assertTrue(result.isRequiresPurchase(), "Quelqu'un doit acheter ce jeu.");
        assertEquals(14.99, result.getPriceToPay(), "Le prix a afficher doit etre 14.99.");
    }
}