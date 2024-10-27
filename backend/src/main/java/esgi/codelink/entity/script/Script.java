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
    private int nbLikes;
    private int nbDislikes;
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

    public Script(User user,ScriptDTO scriptDTO) {
        this.inputFileExtensions = scriptDTO.getInputFileExtensions();
        this.outputFileNames = scriptDTO.getOutputFileNames();
        this.name = scriptDTO.getName();
        this.user = user;
        this.language = scriptDTO.getLanguage();
        this.location = scriptDTO.getLocation();
        this.protectionLevel = ProtectionLevel.valueOf(scriptDTO.getProtectionLevel());
        this.nbLikes = scriptDTO.getNbLikes();
        this.nbDislikes = scriptDTO.getNbDislikes();
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
  
    public void incrementLikes() {
        nbLikes = nbLikes + 1;
    }

    public void decrementLikes() {
        nbLikes = nbLikes - 1;
        if(nbLikes < 0) nbLikes = 0;
    }

    public void incrementDislikes() {
        nbDislikes = nbDislikes + 1;
    }

    public void decrementDislikes() {
        nbDislikes = nbDislikes - 1;
        if(nbDislikes < 0) nbDislikes = 0;
    }

    public int getNbLikes() {
        return nbLikes;
    }

    public int getNbDislikes() {
        return nbDislikes;
    }

    public ScriptDTO toDTO() {
        ScriptDTO dto = new ScriptDTO(this.getUser().getUserId());
        dto.setId(this.getScript_id());
        dto.setName(this.getName());
        dto.setLocation(this.getLocation());
        dto.setProtectionLevel(this.getProtectionLevel().name());
        dto.setLanguage(this.getLanguage());
        dto.setInputFileExtensions(this.getInputFileExtensions());
        dto.setOutputFileNames(this.getOutputFileNames());
        dto.setNbLikes(this.getNbLikes());
        dto.setNbDislikes(this.getNbDislikes());

        return dto;
    }

    public Long getScriptId() {
        return this.script_id;
    }
}
