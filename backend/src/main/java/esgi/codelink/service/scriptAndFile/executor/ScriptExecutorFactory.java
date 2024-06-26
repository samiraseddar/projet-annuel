package esgi.codelink.service.scriptAndFile.executor;

import esgi.codelink.service.scriptAndFile.executor.differentScriptExecutor.PythonScriptExecutor;
import esgi.codelink.service.scriptAndFile.executor.ScriptExecutor;
import org.springframework.stereotype.Service;

@Service
public class ScriptExecutorFactory {

    public ScriptExecutor getExecutor(String language) {
        switch (language.toLowerCase()) {
            case "python":
                return new PythonScriptExecutor();
            // Ajouter des cas pour d'autres langages ici
            default:
                throw new IllegalArgumentException("Unsupported script language: " + language);
        }
    }
}
