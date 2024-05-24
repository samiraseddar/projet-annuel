package esgi.codelink.service.script.differentScriptExecutor;

import esgi.codelink.service.script.ScriptExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class pythonScriptExecutor implements ScriptExecutor { //TODO : faire une classe a hériter pour faire de la généralisation lors de la mise en place d'autre language de script
    @Override
    public String executeScript(String scriptPath) {
        try {
            // Créer un ProcessBuilder pour exécuter le script Python
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath);

            // Démarrer le processus
            Process process = pb.start();

            // Lire la sortie du processus
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Attendre la fin de l'exécution du processus
            int exitCode = process.waitFor();

            // Vérifier le code de sortie
            if (exitCode == 0) {
                return output.toString();
            } else {
                throw new RuntimeException("Erreur lors de l'exécution du script. Code de sortie : " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erreur lors de l'exécution du script", e);
        }
    }

    @Override
    public String executeRawScript(String fullScript){
        try {
            // Créer un ProcessBuilder pour exécuter le script Python directement depuis le contenu
            ProcessBuilder pb = new ProcessBuilder("python");

            // Démarrer le processus
            Process process = pb.start();

            // Envoyer le contenu du script au processus
            process.getOutputStream().write(fullScript.getBytes());
            process.getOutputStream().flush();
            process.getOutputStream().close();

            // Lire la sortie du processus
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Attendre la fin de l'exécution du processus
            int exitCode = process.waitFor();

            // Vérifier le code de sortie
            if (exitCode == 0) {
                return output.toString();
            } else {
                throw new RuntimeException("Erreur lors de l'exécution du script. Code de sortie : " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erreur lors de l'exécution du script", e);
        }
    }

}
