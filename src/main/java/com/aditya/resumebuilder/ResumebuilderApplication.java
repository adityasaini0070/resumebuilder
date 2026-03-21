package com.aditya.resumebuilder;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResumebuilderApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry -> {
			if (entry.getKey() != null && !entry.getKey().isEmpty()) {
				System.setProperty(entry.getKey(), entry.getValue());
			}
		});
		SpringApplication.run(ResumebuilderApplication.class, args);
	}

}
