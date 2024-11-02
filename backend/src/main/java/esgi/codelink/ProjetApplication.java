package esgi.codelink;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ProjetApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjetApplication.class, args);
		log.info(" ===== RUN APPLICATION ===== ");
	}
}
