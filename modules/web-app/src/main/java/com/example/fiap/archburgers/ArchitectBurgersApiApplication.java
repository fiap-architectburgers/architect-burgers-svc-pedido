package com.example.fiap.archburgers;

import com.example.fiap.archburgers.tools.migration.DatabaseMigration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ArchitectBurgersApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArchitectBurgersApiApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onAppStart(ApplicationReadyEvent applicationReadyEvent) {

		DatabaseMigration databaseMigration = applicationReadyEvent.getApplicationContext().getBean(DatabaseMigration.class);
        try {
            databaseMigration.runMigrations();
        } catch (Exception e) {
            throw new RuntimeException("Error running DB migration on app startup: " + e, e);
        }
    }

}
