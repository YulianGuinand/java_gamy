package gamy.daos;

import gamy.models.Game;
import gamy.models.GameMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {

    private static GameDAO gameDAO;

    @BeforeAll
    public static void setUp() {
        gameDAO = new GameDAO();
    }

    @Test
    public void testCrudGame() {
        // CREATE
        Game game = new Game("The Legend of Zelda", GameMode.SOLO);
        game.setPrice(59.99);
        gameDAO.create(game);
        assertNotNull(game.getId(), "L'ID du jeu doit etre genere.");

        // READ
        Game retrievedGame = gameDAO.read(game.getId());
        assertEquals("The Legend of Zelda", retrievedGame.getTitle());
        assertEquals(59.99, retrievedGame.getPrice());
        assertEquals(GameMode.SOLO, retrievedGame.getGameMode());

        // UPDATE
        retrievedGame.setPrice(49.99);
        retrievedGame.setGameMode(GameMode.BOTH);
        gameDAO.update(retrievedGame);

        Game updatedGame = gameDAO.read(game.getId());
        assertEquals(49.99, updatedGame.getPrice());
        assertEquals(GameMode.BOTH, updatedGame.getGameMode());

        // DELETE
        gameDAO.delete(updatedGame);
        assertNull(gameDAO.read(game.getId()), "Le jeu doit etre supprime.");
    }
}