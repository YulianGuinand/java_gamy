# Product Requirements Document (PRD) - Gamy (Gestionnaire de Bibliothèque de Jeux Vidéo)

## 1. Contexte et Objectifs du Projet

**Gamy** est une application de bureau conçue pour les passionnés de jeux vidéo. Elle permet de gérer sa bibliothèque personnelle (jeux possédés, favoris, liste de souhaits), d'interagir avec un réseau d'amis (ajout, suppression, blocage, consultation de leurs bibliothèques) et de résoudre le fameux problème du choix du jeu à plusieurs grâce à un **algorithme de tirage au sort intelligent** (prenant en compte l'intersection des bibliothèques, les filtres de genres/plateformes et le calcul des coûts d'achat si un jeu n'est pas possédé par tout le groupe).

## 2. Stack Technique

* **Langage :** Java 17
* **Interface Graphique :** JavaFX (FXML + CSS)
* **Persistance & ORM :** PostgreSQL & Hibernate ORM (modèle DAO avec `AbstractDAO`)
* **Sécurité :** JBCrypt (hachage des mots de passe)
* **Gestion de projet & Build :** Maven
* **Tests :** JUnit 5

---

## 3. Arborescence du Projet

```text
gamy/
├── src/
│   ├── main/
│   │   ├── java/gamy/
│   │   │   ├── controllers/   # Contrôleurs JavaFX (Login, Register, Main, Friends, Library)
│   │   │   ├── daos/          # Couche d'accès aux données (UserDAO, GameDAO, LibraryDAO, AbstractDAO)
│   │   │   ├── models/        # Entités Hibernate & Objets métiers (User, Game, LibraryEntry, RandomResult, etc.)
│   │   │   ├── services/      # Logique métier (AuthService, UserService, LibraryService, RandomizerService)
│   │   │   ├── utils/         # Utilitaires (HibernateUtil, SceneManager, SessionManager)
│   │   │   ├── Main.java      # Point d'entrée JavaFX
│   │   │   └── Launcher.java  # Lanceur compatible modularité Java 17
│   │   └── resources/
│   │       ├── views/         # Fichiers FXML (Login.fxml, Register.fxml, MainLayout.fxml, Friends.fxml, Library.fxml)
│   │       └── hibernate.cfg.xml # Configuration Hibernate / PostgreSQL
│   └── test/
│       └── java/gamy/         # Tests unitaires et d'intégration JUnit 5 (DAOs, Services)
├── pom.xml                    # Dépendances Maven
└── .gitignore

```

---

## 4. Présentation des Fonctionnalités Clés

1. **Authentification & Sécurité :** Inscription et connexion sécurisées avec hachage de mot de passe (BCrypt). Gestion d'une session utilisateur active (`SessionManager`).
2. **Gestion Sociale (Amis & Blocages) :** Recherche d'utilisateurs par pseudo, ajout d'amis, suppression, blocage et déblocage avec mise à jour en temps réel.
3. **Bibliothèque Personnelle :** Suivi des jeux par statut (*OWNED*, *WISHLIST*, *FAVORITE*) sans doublons.
4. **Tirage au Sort (Randomizer) :**
* *Mode Solo :* Filtres par genres, plateformes, exclusions et modes de jeu.
* *Mode Multijoueur :* Intersection des bibliothèques des participants, inclusion/exclusion manuelle, et estimation du prix d'achat pour les membres ne possédant pas le titre.



---

## 5. Règlements, Bonnes Pratiques & Tests

* **Asynchronisme UI :** Toutes les opérations lourdes (accès BDD, requêtes futures, appels API) doivent impérativement s'exécuter en arrière-plan via `CompletableFuture` et renvoyer les résultats sur le thread principal via `Platform.runLater()` pour éviter le gel de l'interface graphique.
* **Intégrité des données & Tests :**
* Chaque DAO et Service doit disposer d'une couverture par tests unitaires JUnit 5 (`mvn test`).
* Nettoyage rigoureux (`tearDown`) des relations en base pour éviter les violations de clés étrangères sous PostgreSQL.
* Comparaison des entités en base prioritairement par leurs identifiants (`ID`) pour s'affranchir des limites du cache de session Hibernate.



---

## 6. Prochaines Étapes pour Finaliser le Projet

* **Étape 1 : Intégration de l'API RAWG (Phase 5)**
* Développer un client HTTP pour consommer l'API externe RAWG.
* Parser les réponses JSON (avec Jackson) et mapper les données vers les entités locales `Game`, `Genre` et `Platform` en évitant les doublons en base.


* **Étape 2 : Écran de Découverte / Recherche de Jeux (UI)**
* Créer une vue `Discovery.fxml` dotée d'une barre de recherche connectée à l'API RAWG.
* Permettre l'ajout direct d'un jeu trouvé en ligne vers sa bibliothèque personnelle avec choix du statut.


* **Étape 3 : Écran du Tirage au Sort (Randomizer UI)**
* Créer la vue `Randomizer.fxml` permettant de sélectionner le mode (Solo ou Multijoueur), de cocher les filtres (genres, plateformes) et de configurer les listes d'amis participants.
* Afficher le résultat du tirage avec la gestion visuelle du prix si un achat est requis.


* **Étape 4 : Finitions & Thélisation (Phase 6)**
* Appliquer une feuille de style CSS globale (thème sombre / gaming) pour harmoniser l'ensemble des fenêtres.
* Valider l'expérience utilisateur globale et corriger les derniers bugs de navigation.