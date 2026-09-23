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

    @Test
    public void testFindByEmailAndPseudo() {
        // CREATE
        User user = new User("search@gamy.com", "pass123", "Searcher");
        userDAO.create(user);

        // TEST findByEmail
        User foundByEmail = userDAO.findByEmail("search@gamy.com");
        assertNotNull(foundByEmail, "L'utilisateur doit etre trouve par son email.");
        assertEquals("Searcher", foundByEmail.getPseudo());

        // TEST findByPseudo
        User foundByPseudo = userDAO.findByPseudo("Searcher");
        assertNotNull(foundByPseudo, "L'utilisateur doit etre trouve par son pseudo.");
        assertEquals("search@gamy.com", foundByPseudo.getEmail());

        // TEST des requetes introuvables
        assertNull(userDAO.findByEmail("inexistant@gamy.com"));
        assertNull(userDAO.findByPseudo("Ghost"));

        // CLEANUP
        userDAO.delete(user);
    }

    @Test
    public void testFindByIdWithRelations() {
        // CREATE
        User mainUser = new User("main@gamy.com", "pass", "MainUser");
        User friendUser = new User("friend@gamy.com", "pass", "Friend");
        User blockedUser = new User("blocked@gamy.com", "pass", "Blocked");
        
        userDAO.create(mainUser);
        userDAO.create(friendUser);
        userDAO.create(blockedUser);

        // Ajout des relations
        mainUser.getFriends().add(friendUser);
        mainUser.getBlockedUsers().add(blockedUser);
        userDAO.update(mainUser);

        // TEST findByIdWithRelations
        User retrievedUser = userDAO.findByIdWithRelations(mainUser.getId());
        
        assertNotNull(retrievedUser);
        assertEquals(1, retrievedUser.getFriends().size(), "L'utilisateur doit avoir 1 ami.");
        assertEquals("Friend", retrievedUser.getFriends().iterator().next().getPseudo());
        
        assertEquals(1, retrievedUser.getBlockedUsers().size(), "L'utilisateur doit avoir 1 personne bloquee.");
        assertEquals("Blocked", retrievedUser.getBlockedUsers().iterator().next().getPseudo());

        // CLEANUP (vider les relations avant de supprimer pour eviter les erreurs de cles etrangeres)
        retrievedUser.getFriends().clear();
        retrievedUser.getBlockedUsers().clear();
        userDAO.update(retrievedUser);
        
        userDAO.delete(retrievedUser);
        userDAO.delete(friendUser);
        userDAO.delete(blockedUser);
    }
}
