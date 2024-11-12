package net.krusher.datalinks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Datalinks {

	public static void main(String[] args) {
		SpringApplication.run(Datalinks.class, args);
	}

}
