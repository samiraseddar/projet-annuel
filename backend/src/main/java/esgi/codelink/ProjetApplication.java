package esgi.codelink;

import esgi.codelink.service.script.ScriptExecutor;
import esgi.codelink.service.script.differentScriptExecutor.pythonScriptExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Paths;

@SpringBootApplication
public class ProjetApplication {

	public static void main(String[] args) {
		System.out.println("hello world");
		ScriptExecutor executor = new pythonScriptExecutor();
		String pathActuel = Paths.get("").toString();
		System.out.println("relativePath = " + pathActuel);
		String scriptPath = "C:\\jujutravail\\coursESGIpresentiel\\2023-2024\\semestre2\\projetAnnuel\\origine\\projet-annuel\\backend\\src\\main\\script\\python\\script.py";
		System.out.println("path = " + scriptPath);

		//String result = executor.executeScript(scriptPath);
		//mettre test ici

		// Afficher le résultat
		System.out.println("Résultat de l'exécution du script Python :");
		//System.out.println(result);

		SpringApplication.run(ProjetApplication.class, args);
	}

}
