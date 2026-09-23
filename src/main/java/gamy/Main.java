package gamy;

import gamy.utils.HibernateUtil;

import org.hibernate.Session;

public class Main {
    public static void main(String[] args) {
        System.out.println("Lancement du test de connexion Hibernate...");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("Connexion à la base de données réussie !");
            System.out.println("Hibernate a pu communiquer avec PostgreSQL.");
        } catch (Exception e) {
            System.err.println("Erreur de connexion à la base de données :");
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}