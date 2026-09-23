package gamy.services;

import gamy.daos.LibraryDAO;
import gamy.daos.UserDAO;
import gamy.models.LibraryEntry;
import gamy.models.User;
import java.util.List;

public class UserService {

    private final UserDAO userDAO;
    private final LibraryDAO libraryDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.libraryDAO = new LibraryDAO();
    }

    public UserService(UserDAO userDAO, LibraryDAO libraryDAO) {
        this.userDAO = userDAO;
        this.libraryDAO = libraryDAO;
    }

    public void addFriend(Long currentUserId, Long friendId) throws Exception {
        if (currentUserId.equals(friendId)) {
            throw new Exception("Vous ne pouvez pas vous ajouter vous-meme en ami.");
        }

        User currentUser = userDAO.findByIdWithRelations(currentUserId);
        User friend = userDAO.read(friendId);

        if (currentUser == null || friend == null) {
            throw new Exception("Utilisateur introuvable.");
        }

        boolean isBlocked = currentUser.getBlockedUsers().stream()
                .anyMatch(u -> u.getId().equals(friendId));
        if (isBlocked) {
            throw new Exception("Vous avez bloque cet utilisateur. Debloquez-le d'abord.");
        }

        currentUser.getFriends().add(friend);
        userDAO.update(currentUser);
    }

    public void removeFriend(Long currentUserId, Long friendId) throws Exception {
        User currentUser = userDAO.findByIdWithRelations(currentUserId);

        if (currentUser != null) {
            currentUser.getFriends().removeIf(f -> f.getId().equals(friendId));
            userDAO.update(currentUser);
        }
    }

    public void blockUser(Long currentUserId, Long userToBlockId) throws Exception {
        if (currentUserId.equals(userToBlockId)) {
            throw new Exception("Vous ne pouvez pas vous bloquer vous-meme.");
        }

        User currentUser = userDAO.findByIdWithRelations(currentUserId);
        User userToBlock = userDAO.read(userToBlockId);

        if (currentUser != null && userToBlock != null) {
            currentUser.getFriends().removeIf(f -> f.getId().equals(userToBlockId));
            currentUser.getBlockedUsers().add(userToBlock);
            userDAO.update(currentUser);
        }
    }

    public List<LibraryEntry> getFriendLibrary(Long currentUserId, Long friendId) throws Exception {
        User currentUser = userDAO.findByIdWithRelations(currentUserId);

        boolean isFriend = currentUser.getFriends().stream()
                .anyMatch(u -> u.getId().equals(friendId));

        if (!isFriend) {
            throw new Exception("Vous devez etre ami avec cet utilisateur pour voir sa bibliotheque.");
        }

        return libraryDAO.findByUserId(friendId);
    }
}