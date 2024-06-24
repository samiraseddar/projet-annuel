package esgi.codelink;

import esgi.codelink.service.script.ScriptExecutor;
import esgi.codelink.service.script.differentScriptExecutor.pythonScriptExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Paths;

@SpringBootApplication
public class ProjetApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetApplication.class, args);
	}

}
