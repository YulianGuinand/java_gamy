package gamy.services;

import gamy.daos.UserDAO;
import gamy.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private AuthService authService;
    private UserDAO userDAO;
    private User testUser;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
        authService = new AuthService(userDAO);
    }

    @AfterEach
    public void tearDown() {
        // CLEANUP
        if (testUser != null && testUser.getId() != null) {
            userDAO.delete(testUser);
        }
    }

    @Test
    public void testRegistrationAndLoginSuccess() throws Exception {
        // REGISTER
        testUser = authService.register("auth@gamy.com", "secret123", "AuthMaster");
        assertNotNull(testUser.getId(), "L'utilisateur doit etre enregistre en base.");
        
        // MDP validation
        assertNotEquals("secret123", testUser.getPassword(), "Le mot de passe doit etre hache.");

        // LOGIN
        User loggedInUser = authService.login("auth@gamy.com", "secret123");
        assertNotNull(loggedInUser, "La connexion doit reussir.");
        assertEquals(testUser.getId(), authService.getCurrentUser().getId(), "La session doit etre active.");
    }

    @Test
    public void testLoginFailureWrongPassword() throws Exception {
        testUser = authService.register("fail@gamy.com", "correctpass", "FailTester");
        
        Exception exception = assertThrows(Exception.class, () -> {
            authService.login("fail@gamy.com", "wrongpass");
        });
        assertEquals("Identifiants incorrects.", exception.getMessage());
    }

    @Test
    public void testRegistrationDuplicateEmail() throws Exception {
        testUser = authService.register("dup@gamy.com", "pass", "Original");
        
        Exception exception = assertThrows(Exception.class, () -> {
            authService.register("dup@gamy.com", "pass2", "Copycat");
        });
        assertEquals("Cet email est deja utilise.", exception.getMessage());
    }
}