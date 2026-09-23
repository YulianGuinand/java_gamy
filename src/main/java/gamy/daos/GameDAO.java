package gamy.daos;

import org.hibernate.Session;
import org.hibernate.query.Query;
import gamy.models.Game;
import gamy.utils.HibernateUtil;
import org.hibernate.Transaction;

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

    public void saveOrUpdateGame(Game game) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Game existing = session.createQuery("FROM Game g WHERE g.title = :title", Game.class)
                    .setParameter("title", game.getTitle())
                    .uniqueResult();

            if (existing == null) {
                session.persist(game);
            }
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}