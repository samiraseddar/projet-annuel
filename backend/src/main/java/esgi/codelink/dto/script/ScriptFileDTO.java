package esgi.codelink.dto.script;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

public class ScriptFileDTO {

    @Null
    private Long id;
    @NotBlank
    private String name;
    @Null
    @JsonIgnore
    private String path;

    private Boolean isGenerated;
    @Null
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getGenerated() {
        return isGenerated;
    }

    public void setGenerated(Boolean generated) {
        isGenerated = generated;
    }
}
