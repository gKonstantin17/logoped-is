package logopedis.rsmain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {
		"logopedis.rsmain",
		"logopedis.libutils"   // добавляем новый модуль
})
@EntityScan(basePackages = "logopedis.libentities.rsmain.entity")
@EnableJpaRepositories(basePackages = "logopedis.rsmain.repository")

public class RsMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(RsMainApplication.class, args);
	}

}
