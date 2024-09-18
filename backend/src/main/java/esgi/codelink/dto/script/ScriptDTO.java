package esgi.codelink.dto.script;

public class ScriptDTO {

    private Long id;
    private String name;
    private String location;
    private String protectionLevel;
    private String language;
    private String inputFileExtensions;
    private String outputFileNames;
    private Long userId;

    private int nbLikes;

    private int nbDislikes;


    // Getters

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

    public String getInputFileExtensions() {
        return inputFileExtensions;
    }

    public String getOutputFileNames() {
        return outputFileNames;
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

    public void setInputFileExtensions(String inputFileExtensions) {
        this.inputFileExtensions = inputFileExtensions;
    }

    public void setOutputFileNames(String outputFileNames) {
        this.outputFileNames = outputFileNames;
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
}
