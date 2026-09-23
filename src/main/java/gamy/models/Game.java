package gamy.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_id", unique = true)
    private Long apiId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private Double price;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_mode", nullable = false)
    private GameMode gameMode = GameMode.BOTH;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "game_genres",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "game_platforms",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "platform_id")
    )
    private Set<Platform> platforms = new HashSet<>();

    public Game() {}

    public Game(String _title, GameMode _gameMode) {
        this.title = _title;
        this.gameMode = _gameMode;
    }

    public Long getId() { return id; }
    public void setId(Long _id) { this.id = _id; }
    
    public Long getApiId() { return apiId; }
    public void setApiId(Long _apiId) { this.apiId = _apiId; }

    public String getTitle() { return title; }
    public void setTitle(String _title) { this.title = _title; }

    public String getDescription() { return description; }
    public void setDescription(String _description) { this.description = _description; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate _releaseDate) { this.releaseDate = _releaseDate; }

    public Double getPrice() { return price; }
    public void setPrice(Double _price) { this.price = _price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String _imageUrl) { this.imageUrl = _imageUrl; }

    public GameMode getGameMode() { return gameMode; }
    public void setGameMode(GameMode _gameMode) { this.gameMode = _gameMode; }

    public Set<Genre> getGenres() { return genres; }
    public void setGenres(Set<Genre> _genres) { this.genres = _genres; }

    public Set<Platform> getPlatforms() { return platforms; }
    public void setPlatforms(Set<Platform> _platforms) { this.platforms = _platforms; }
}