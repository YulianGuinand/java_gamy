package gamy.models;

import jakarta.persistence.*;

@Entity
@Table(name = "library_entries", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "game_id"})
})
public class LibraryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LibraryStatus status;

    public LibraryEntry() {}

    public LibraryEntry(User _user, Game _game, LibraryStatus _status) {
        this.user = _user;
        this.game = _game;
        this.status = _status;
    }

    public Long getId() { return id; }
    public void setId(Long _id) { this.id = _id; }

    public User getUser() { return user; }
    public void setUser(User _user) { this.user = _user; }

    public Game getGame() { return game; }
    public void setGame(Game _game) { this.game = _game; }

    public LibraryStatus getStatus() { return status; }
    public void setStatus(LibraryStatus _status) { this.status = _status; }
}