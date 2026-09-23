package gamy.models;

import jakarta.persistence.*;

@Entity
@Table(name = "platforms")
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    public Platform() {}

    public Platform(String _name) {
        this.name = _name;
    }

    public Long getId() { return id; }
    public void setId(Long _id) { this.id = _id; }
    public String getName() { return name; }
    public void setName(String _name) { this.name = _name; }
}