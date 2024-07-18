package esgi.codelink.entity.script;

import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.entity.User;
import esgi.codelink.enumeration.ProtectionLevel;
import jakarta.persistence.*;

@Entity
@Table(name = "scripts")
public class Script {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "script_id")
    private Long script_id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProtectionLevel protectionLevel;

    @Column(nullable = false)
    private String language;

    @Column
    private String inputFileExtensions;

    @Column
    private String outputFileNames;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Script(ScriptDTO scriptToCopie, User user) {
        this.inputFileExtensions = scriptToCopie.getInputFileExtensions();
        this.outputFileNames = scriptToCopie.getOutputFileNames();
        this.name = scriptToCopie.getName();
        this.user = user;
        this.language = scriptToCopie.getLanguage();
        this.location = scriptToCopie.getLocation();
        this.protectionLevel = ProtectionLevel.valueOf(scriptToCopie.getProtectionLevel());
    }

    public Script() {
        this.inputFileExtensions = "";
        this.outputFileNames = "";
        this.name = "";
        this.language = "";
        this.location = "";
        this.protectionLevel = ProtectionLevel.PRIVATE;
    }

    // Getters and Setters

    public Long getScript_id() {
        return script_id;
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

    public ProtectionLevel getProtectionLevel() {
        return protectionLevel;
    }

    public void setProtectionLevel(ProtectionLevel protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getInputFileExtensions() {
        return inputFileExtensions;
    }

    public void setInputFileExtensions(String inputFileExtensions) {
        this.inputFileExtensions = inputFileExtensions;
    }

    public String getOutputFileNames() {
        return outputFileNames;
    }

    public void setOutputFileNames(String outputFileNames) {
        this.outputFileNames = outputFileNames;
    }

    public User getUser() {
        return user;
    }
}
