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

    @Column(nullable = false/*, unique = true*/)    //necessaire sinon les fichiers sont écrasés. Donc pourrait aussi servir d'id
    private String name;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProtectionLevel protectionLevel;

    @Column(nullable = false)
    private String language;

    @Column
    private String inputFiles;

    @Column
    private String outputFiles;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Script(ScriptDTO scriptToCopie, User user){
        this.script_id = scriptToCopie.getId();
        this.inputFiles = scriptToCopie.getInputFiles();
        this.outputFiles = scriptToCopie.getOutputFiles();
        this.name = scriptToCopie.getName();
        this.user = user;
        this.language = scriptToCopie.getLanguage();
        this.location = scriptToCopie.getLocation();
        this.protectionLevel = ProtectionLevel.PRIVATE; //TODO
    }

    public Script(){
        this.script_id = null;  //to rework
        this.inputFiles = "";
        this.outputFiles = "";
        this.name = "";
        this.user = null;
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

    public String getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(String inputFiles) {
        this.inputFiles = inputFiles;
    }

    public String getOutputFiles() {
        return outputFiles;
    }

    public void setOutputFiles(String outputFiles) {
        this.outputFiles = outputFiles;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
