package gamy.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity 
@Table(name = "users")
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String pseudo;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany 
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "user_blocked",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "blocked_id")
    )
    private Set<User> blockedUsers = new HashSet<>();

    public User() {}

    public User(String _email, String _password, String _pseudo) {
        this.email = _email;
        this.password = _password;
        this.pseudo = _pseudo;
    }

    public Long getId() { return this.id; }
    public void setId(Long _id) { this.id = _id; }

    public String getEmail() { return this.email; }
    public void setEmail(String _email) { this.email = _email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<User> getFriends() { return friends; }
    public void setFriends(Set<User> friends) { this.friends = friends; }

    public Set<User> getBlockedUsers() { return blockedUsers; }
    public void setBlockedUsers(Set<User> blockedUsers) { this.blockedUsers = blockedUsers; }
}
