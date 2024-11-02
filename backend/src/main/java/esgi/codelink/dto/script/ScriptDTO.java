package esgi.codelink.dto.script;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class ScriptDTO {

    private Long id;
    private String name;

    @JsonIgnore
    private String location;

    private String protectionLevel;
    private String language;
    private Long userId;

    private int nbLikes;

    private int nbDislikes;


    // Getters

    public ScriptDTO(){}

    public ScriptDTO(long userId){
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getProtectionLevel() {
        return protectionLevel;
    }

    public String getLanguage() {
        return language;
    }


    public Long getUserId() {
        return userId;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setProtectionLevel(String protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    public int getNbLikes() {
        return nbLikes;
    }

    public void setNbLikes(int nbLikes) {
        this.nbLikes = nbLikes;
    }

    public int getNbDislikes() {
        return nbDislikes;
    }

    public void setNbDislikes(int nbDislikes) {
        this.nbDislikes = nbDislikes;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Méthodes pour gérer likes et dislikes
    public void incrementLikes() {
        this.nbLikes += 1;
    }

    public void decrementLikes() {
        if (this.nbLikes > 0) {
            this.nbLikes -= 1;
        }
    }

    public void incrementDislikes() {
        this.nbDislikes += 1;
    }

    public void decrementDislikes() {
        if (this.nbDislikes > 0) {
            this.nbDislikes -= 1;
        }
    }

    @Override
    public String toString() {
        return "ScriptDTO{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", protectionLevel='" + protectionLevel + '\'' +
                ", language='" + language + '\'' +
                ", userId=" + userId +
                '}';
    }
}
