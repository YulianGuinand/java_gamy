package gamy.daos;

import gamy.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    
    private static UserDAO userDAO;

    @BeforeAll
    public static void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    public void testCreateReadAndDeleteUser() {
        // CREATE
        User testUser = new User("test@gamy.com", "motdepasse123", "TestPlayer");
        userDAO.create(testUser);

        assertNotNull(testUser.getId(), "L'ID devrait etre genere par la base de donnees.");

        // READ
        User retrievedUser = userDAO.read(testUser.getId());
        assertNotNull(retrievedUser, "L'utilisateur devrait etre trouve en base.");
        assertEquals("test@gamy.com", retrievedUser.getEmail(), "L'email doit correspondre.");
        assertEquals("TestPlayer", retrievedUser.getPseudo(), "Le pseudo doit correspondre.");

        // DELETE
        userDAO.delete(retrievedUser);

        User deletedUser = userDAO.read(testUser.getId());
        assertNull(deletedUser, "L'utilisateur devrait etre supprime de la base.");
    }

    @Test
    public void testUpdateUser() {
        // CREATE
        User user = new User("update@gamy.com", "pass", "AncienPseudo");
        userDAO.create(user);

        // EDIT
        user.setPseudo("NouveauPseudo");
        user.setDescription("Ceci est une description mise a jour.");
        
        // UPDATE
        userDAO.update(user);

        // READ
        User updatedUser = userDAO.read(user.getId());
        assertEquals("NouveauPseudo", updatedUser.getPseudo(), "Le pseudo doit avoir change.");
        assertEquals("Ceci est une description mise a jour.", updatedUser.getDescription(), "La description doit avoir change.");

        // CLEANUP
        userDAO.delete(updatedUser);
    }
}
