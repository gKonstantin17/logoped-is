package logopedis.msnotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "logopedis.msnotification",
        "logopedis.libutils"   // добавляем новый модуль
})
@EntityScan(basePackages = "logopedis.libentities.msnotification.entity")
@EnableJpaRepositories(basePackages = "logopedis.msnotification.repository")
public class MsNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsNotificationApplication.class, args);
    }

}
