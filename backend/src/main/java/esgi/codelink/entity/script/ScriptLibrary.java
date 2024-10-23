package esgi.codelink.entity;

import esgi.codelink.entity.script.Script;
import esgi.codelink.enumeration.ProtectionLevel;
import jakarta.persistence.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "script_libraries")
public class ScriptLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "library_id")
    private Long libraryId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProtectionLevel protectionLevel;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "library_scripts",
            joinColumns = @JoinColumn(name = "library_id"),
            inverseJoinColumns = @JoinColumn(name = "script_id")
    )
    private Set<Script> scripts = new HashSet<>();

    // Constructors
    public ScriptLibrary() {};

    public ScriptLibrary(User owner) {
        this.owner = owner;
    }

    public ScriptLibrary(String name, User owner, ProtectionLevel protectionLevel) {
        this.name = name;
        this.owner = owner;
        this.protectionLevel = protectionLevel;
    }

    // Getters and Setters
    public Long getLibraryId() {
        return libraryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProtectionLevel getProtectionLevel() {
        return protectionLevel;
    }

    public void setProtectionLevel(ProtectionLevel protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    public User getOwner() {
        return owner;
    }

    public Set<Script> getScripts() {
        return scripts;
    }

    public void addScript(Script script) {
        this.scripts.add(script);
    }

    public void removeScript(Script script) {
        this.scripts.remove(script);
    }
}
