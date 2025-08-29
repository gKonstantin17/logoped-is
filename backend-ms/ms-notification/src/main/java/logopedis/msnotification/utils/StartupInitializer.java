package logopedis.msnotification.utils;

import logopedis.msnotification.service.LessonIntegrationService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupInitializer implements ApplicationRunner {
    private final LessonIntegrationService integrationService;

    public StartupInitializer(LessonIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @Override
    public void run(ApplicationArguments args) {
        integrationService.fetchUpcomingLessons(); // подтягиваем ближайшие 2 дня
    }
}
