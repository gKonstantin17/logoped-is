package gk17.rsmain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RsMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(RsMainApplication.class, args);
	}

}
