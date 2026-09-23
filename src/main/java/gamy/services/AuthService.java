package gamy.services;

import gamy.daos.UserDAO;
import gamy.models.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    
    private final UserDAO userDAO;
    private User currentUser;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(UserDAO _userDAO) {
        this.userDAO = _userDAO;
    }

    public User register(String email, String password, String pseudo) throws Exception {
        if (userDAO.findByEmail(email) != null) {
            throw new Exception("Cet email est deja utilise.");
        }

        if (userDAO.findByPseudo(pseudo) != null) {
            throw new Exception("Ce pseudo est deja pris.");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User newUser = new User(email, hashedPassword, pseudo);
        userDAO.create(newUser);

        return newUser;
    }

    public User login(String email, String password) throws Exception {
        User user = userDAO.findByEmail(email);

        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new Exception("Identifiants incorrects.");
        }

        this.currentUser = user;
        return user;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }
}
