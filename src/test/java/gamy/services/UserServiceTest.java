package gamy.services;

import gamy.daos.LibraryDAO;
import gamy.daos.UserDAO;
import gamy.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private UserDAO userDAO;
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
        userService = new UserService(userDAO, new LibraryDAO());

        long time = System.currentTimeMillis();
        user1 = new User("user1_" + time + "@gamy.com", "pass", "P1_" + time);
        user2 = new User("user2_" + time + "@gamy.com", "pass", "P2_" + time);
        userDAO.create(user1);
        userDAO.create(user2);
    }

    @AfterEach
    public void tearDown() {
        if (user1 != null && user1.getId() != null) {
            User u1 = userDAO.findByIdWithRelations(user1.getId());
            if (u1 != null) {
                u1.getFriends().clear();
                u1.getBlockedUsers().clear();
                userDAO.update(u1);
                userDAO.delete(u1);
            }
        }
        if (user2 != null && user2.getId() != null) {
            User u2 = userDAO.findByIdWithRelations(user2.getId());
            if (u2 != null) {
                u2.getFriends().clear();
                u2.getBlockedUsers().clear();
                userDAO.update(u2);
                userDAO.delete(u2);
            }
        }
    }

    @Test
    public void testAddAndRemoveFriend() throws Exception {
        // ADD FRIEND
        userService.addFriend(user1.getId(), user2.getId());
        User updatedUser1 = userDAO.findByIdWithRelations(user1.getId());
        
        boolean isFriend = updatedUser1.getFriends().stream().anyMatch(u -> u.getId().equals(user2.getId()));
        assertTrue(isFriend, "PlayerTwo devrait etre dans la liste d'amis de PlayerOne.");

        // DELETE FRIEND
        userService.removeFriend(user1.getId(), user2.getId());
        User user1AfterRemoval = userDAO.findByIdWithRelations(user1.getId());
        
        boolean isStillFriend = user1AfterRemoval.getFriends().stream().anyMatch(u -> u.getId().equals(user2.getId()));
        assertFalse(isStillFriend, "PlayerTwo ne devrait plus etre ami avec PlayerOne.");
    }

    @Test
    public void testBlockUserRemovesFromFriends() throws Exception {
        userService.addFriend(user1.getId(), user2.getId());
        userService.blockUser(user1.getId(), user2.getId());
        
        User updatedUser1 = userDAO.findByIdWithRelations(user1.getId());
        
        boolean isFriend = updatedUser1.getFriends().stream().anyMatch(u -> u.getId().equals(user2.getId()));
        boolean isBlocked = updatedUser1.getBlockedUsers().stream().anyMatch(u -> u.getId().equals(user2.getId()));
        
        assertFalse(isFriend, "Le blocage doit retirer l'utilisateur des amis.");
        assertTrue(isBlocked, "L'utilisateur doit etre dans la liste des bloques.");
    }

    @Test
    public void testCannotAddSelfAsFriend() {
        Exception exception = assertThrows(Exception.class, () -> {
            userService.addFriend(user1.getId(), user1.getId());
        });
        assertEquals("Vous ne pouvez pas vous ajouter vous-meme en ami.", exception.getMessage());
    }
}