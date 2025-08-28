package logopedis.libutils.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic lessonNoteTopic() {
        return TopicBuilder.name("lesson-note-topic")
                .partitions(3)        // количество partition-ов
                .replicas(1)          // количество реплик
                .build();
    }
    @Bean
    public NewTopic lessonRequestTopic() {
        return TopicBuilder.name("lesson-request-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

}
