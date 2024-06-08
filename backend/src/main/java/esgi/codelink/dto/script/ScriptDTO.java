package esgi.codelink.dto.script;


public class ScriptDTO {
    private Long id;
    private String name;
    private String location;
    private String protectionLevel;
    private String language;
    private String inputFiles;
    private String outputFiles;
    private Long userId;


    // Getters and Setters

    public ScriptDTO() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

//      I don't want ID to be changed
//    public void setId(Long id) {
//        this.id = id;
//    }

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

    public String getProtectionLevel() {
        return protectionLevel;
    }

    public void setProtectionLevel(String protectionLevel) {
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

    public void setScriptId(Long scriptId) {
        this.id = scriptId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId(){
        return this.userId;
    }
}
