package esgi.codelink.entity.script;

import esgi.codelink.entity.User;
import jakarta.persistence.*;

@Entity
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private boolean isGenerated;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public File() {
        // Constructor par défaut requis par JPA
    }

    public File(String name, String location, boolean isGenerated, User user) {
        this.name = name;
        this.location = location;
        this.isGenerated = isGenerated;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public void setGenerated(boolean generated) {
        isGenerated = generated;
    }

    public User getUser() {
        return user;
    }

    // Pas de setter pour user pour éviter les modifications non désirées

    // Autres getters et setters
}