package logopedis.libutils.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    public static final String lessonNoteTopic = "lesson-note-topic";
    public static final String lessonRequestTopic = "lesson-request-topic";
    public static final String lessonStatusTopic = "lesson-status-topic";

    @Bean
    public NewTopic lessonNoteTopic() {
        return TopicBuilder.name(lessonNoteTopic)
                .partitions(3)        // количество partition-ов
                .replicas(1)          // количество реплик
                .build();
    }
    @Bean
    public NewTopic lessonRequestTopic() {
        return TopicBuilder.name(lessonRequestTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic lessonStatusTopic() {
        return TopicBuilder.name(lessonStatusTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

}
