package gamy.daos;

import gamy.models.LibraryEntry;
import gamy.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

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
}