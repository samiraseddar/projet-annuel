package esgi.codelink.entity.script;

import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.entity.User;
import esgi.codelink.enumeration.ProtectionLevel;
import esgi.codelink.service.pipeline.ScriptLanguage;
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

    @Enumerated(EnumType.STRING)
    private ScriptLanguage language;

    private int nbLikes;
    private int nbDislikes;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Script(ScriptDTO scriptDTO, User user) {
        this.name = scriptDTO.getName();
        this.location = scriptDTO.getLocation();
        this.user = user;
        this.language = ScriptLanguage.fromLocation(scriptDTO.getLanguage());
        this.protectionLevel = ProtectionLevel.valueOf(scriptDTO.getProtectionLevel());
        this.nbLikes = scriptDTO.getNbLikes();
        this.nbDislikes = scriptDTO.getNbDislikes();
    }

    public Script() {
        this.name = "";
        this.language = ScriptLanguage.UNKNOWN;
        this.location = "";
        this.protectionLevel = ProtectionLevel.PRIVATE;
    }

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

    public ScriptLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ScriptLanguage language) {
        this.language = language;
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
        dto.setLanguage(this.getLanguage().name());
        dto.setNbLikes(this.getNbLikes());
        dto.setNbDislikes(this.getNbDislikes());

        return dto;
    }

    public Long getScriptId() {
        return this.script_id;
    }

    @Override
    public String toString() {
        return "Script{" +
                "script_id=" + script_id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", protectionLevel=" + protectionLevel +
                ", language=" + language +
                '}';
    }
}
