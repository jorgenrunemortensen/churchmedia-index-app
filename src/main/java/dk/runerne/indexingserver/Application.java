package dk.runerne.indexingserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the indexing-server Spring Boot application.
 */
@SpringBootApplication
public class Application {

	/**
	 * The main method to start the Spring Boot application.
	 *
	 * @param args command-line argumentsF
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}