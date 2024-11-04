package esgi.codelink.service.pipeline;

public enum ScriptLanguage {
    PYTHON(".py"),
    JAVASCRIPT(".js"),
    JAVA(".java"),
    RUBY(".rb"),
    UNKNOWN("");

    private final String extension;

    ScriptLanguage(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static ScriptLanguage fromLocation(String language) {
        return switch (language.toLowerCase()) {
            case "python" -> PYTHON;
            case "javascript" -> JAVASCRIPT;
            default -> UNKNOWN;
        };
    }
}
