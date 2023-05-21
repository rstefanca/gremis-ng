package cz.upce.gremis.gremisng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GremisNgApplication {

	public static void main(String[] args) {
		SpringApplication.run(GremisNgApplication.class, args);
	}

}
