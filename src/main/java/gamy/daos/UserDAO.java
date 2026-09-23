package gamy.daos;

import gamy.models.User;
import gamy.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserDAO extends AbstractDAO<User> {
    
    public UserDAO() {
        super(User.class);
    }

    public User findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        }
    }

    public User findByPseudo(String pseudo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE pseudo = :pseudo", User.class);
            query.setParameter("pseudo", pseudo);
            return query.uniqueResult();
        }
    }

    public User findByIdWithRelations(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                "SELECT DISTINCT u FROM User u " +
                "LEFT JOIN FETCH u.friends " +
                "LEFT JOIN FETCH u.blockedUsers " +
                "WHERE u.id = :id", User.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        }
    }
}
