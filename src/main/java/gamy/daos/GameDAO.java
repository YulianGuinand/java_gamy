package gamy.daos;

import org.hibernate.Session;
import org.hibernate.query.Query;
import gamy.models.Game;
import gamy.models.GamePlatform;
import gamy.models.Genre;
import gamy.utils.HibernateUtil;
import javafx.application.Platform;

import org.hibernate.Transaction;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GameDAO extends AbstractDAO<Game> {

    public GameDAO() {
        super(Game.class);
    }

    public Optional<Game> findByTitle(String title) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Game game = session.createQuery("FROM Game g WHERE g.title = :title", Game.class)
                    .setParameter("title", title)
                    .uniqueResult();
            return Optional.ofNullable(game);
        }
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

    public void updateGame(Game game) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(game);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Game saveOrUpdateGame(Game game) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Game existing = null;
            if (game.getApiId() != null) {
                existing = session.createQuery("FROM Game g WHERE g.apiId = :apiId", Game.class)
                        .setParameter("apiId", game.getApiId())
                        .uniqueResult();
            }
            if (existing == null && game.getTitle() != null) {
                existing = session.createQuery("FROM Game g WHERE g.title = :title", Game.class)
                        .setParameter("title", game.getTitle())
                        .uniqueResult();
            }

            if (existing != null) {
                transaction.commit();
                return existing;
            }

            Set<Genre> managedGenres = new HashSet<>();
            for (Genre genre : game.getGenres()) {
                Genre dbGenre = session.createQuery("FROM Genre g WHERE g.name = :name", Genre.class)
                        .setParameter("name", genre.getName())
                        .uniqueResult();
                if (dbGenre == null) {
                    session.persist(genre);
                    managedGenres.add(genre);
                } else {
                    managedGenres.add(dbGenre);
                }
            }
            game.setGenres(managedGenres);

            Set<GamePlatform> managedPlatforms = new HashSet<>();
            for (GamePlatform platform : game.getPlatforms()) {
                GamePlatform dbPlatform = session.createQuery("FROM GamePlatform p WHERE p.name = :name", GamePlatform.class)
                        .setParameter("name", platform.getName())
                        .uniqueResult();
                if (dbPlatform == null) {
                    session.persist(platform);
                    managedPlatforms.add(platform);
                } else {
                    managedPlatforms.add(dbPlatform);
                }
            }
            game.setPlatforms(managedPlatforms);

            session.persist(game);
            transaction.commit();
            return game;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return null;
        }
    }
}