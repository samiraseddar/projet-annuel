package esgi.codelink.dto.file;

import esgi.codelink.dto.script.FileDTO;

public class FileRequest {

    private FileDTO fileDTO;
    private String fileContent;

    // Getters and Setters
    public FileDTO getFileDTO() {
        return fileDTO;
    }

    public String getFileContent() {
        return fileContent;
    }
}
