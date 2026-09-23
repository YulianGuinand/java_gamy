package gamy.daos;

import org.hibernate.Session;
import org.hibernate.query.Query;
import gamy.models.Game;

public class GameDAO extends AbstractDAO<Game> {

    public GameDAO() {
        super(Game.class);
    }

    public Game findByIdWithRelations(Long id) {
        try (Session session = gamy.utils.HibernateUtil.getSessionFactory().openSession()) {
            Query<Game> query = session.createQuery(
                "SELECT DISTINCT g FROM Game g " +
                "LEFT JOIN FETCH g.genres " +
                "LEFT JOIN FETCH g.platforms " +
                "WHERE g.id = :id", Game.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        }
    }
}