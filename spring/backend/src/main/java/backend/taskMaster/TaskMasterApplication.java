package backend.taskMaster;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskMasterApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TaskMasterApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Running application. Checking for repositories.....");
	}

}
