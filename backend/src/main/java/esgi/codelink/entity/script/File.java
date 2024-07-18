package esgi.codelink.entity.script;

import esgi.codelink.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private boolean isGenerated;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public File(String name, String location, boolean isGenerated, User user) {
        this.name = name;
        this.location = location;
        this.isGenerated = isGenerated;
        this.user = user;
    }

    public File() {
        // Default constructor
    }

    public File(Long id, String name, String location, boolean isGenerated, User user) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.isGenerated = isGenerated;
        this.user = user;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public User getUser() {
        return user;
    }
}
