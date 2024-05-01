package esgi.codelink;

import esgi.codelink.service.ScriptExecutor;
import esgi.codelink.service.differentScriptExecutor.pythonScriptExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Paths;

@SpringBootApplication
public class ProjetApplication {

	public static void main(String[] args) {
		System.out.println("hello world");
		ScriptExecutor executor = new pythonScriptExecutor();
		String scriptPath = "C:\\jujutravail\\coursESGIpresentiel\\2023-2024\\semestre2\\projetAnnuel\\origine\\projet-annuel\\backend\\src\\main\\script\\python\\script.py";
		System.out.println("path = " + scriptPath);

		String result = executor.executeScript(scriptPath);
		//mettre test ici

		// Afficher le résultat
		System.out.println("Résultat de l'exécution du script Python :");
		System.out.println(result);

		SpringApplication.run(ProjetApplication.class, args);
	}

}
