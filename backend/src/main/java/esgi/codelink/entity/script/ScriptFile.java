package esgi.codelink.entity.script;

import esgi.codelink.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "script_files")
public class ScriptFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String path;
    private boolean isGenerated; // true pour output, false pour input

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // getters et setters


    public ScriptFile(User user) {
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
}
