package gamy.daos;

import gamy.models.LibraryEntry;
import gamy.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import org.hibernate.Transaction;

public class LibraryDAO extends AbstractDAO<LibraryEntry> {

    public LibraryDAO() {
        super(LibraryEntry.class);
    }

    public List<LibraryEntry> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<LibraryEntry> query = session.createQuery("FROM LibraryEntry le JOIN FETCH le.game WHERE le.user.id = :userId", LibraryEntry.class);
            query.setParameter("userId", userId);
            return query.list();
        }
    }

    public LibraryEntry findByUserAndGame(Long userId, Long gameId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<LibraryEntry> query = session.createQuery(
                "FROM LibraryEntry le WHERE le.user.id = :userId AND le.game.id = :gameId", 
                LibraryEntry.class);
            query.setParameter("userId", userId);
            query.setParameter("gameId", gameId);
            return query.uniqueResult();
        }
    }

    public void save(LibraryEntry entry) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(entry);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public void delete(LibraryEntry entry) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(session.contains(entry) ? entry : session.merge(entry));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void updateEntryAndGame(LibraryEntry entry) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            session.merge(entry);
            if (entry.getGame() != null) {
                session.merge(entry.getGame());
            }
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}