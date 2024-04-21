package esgi.codelink.dto;

public class RegisterResponseDTO {
    private String status;
    private String error;

    public RegisterResponseDTO(String status, String error) {
        this.status = status;
        this.error = error;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}