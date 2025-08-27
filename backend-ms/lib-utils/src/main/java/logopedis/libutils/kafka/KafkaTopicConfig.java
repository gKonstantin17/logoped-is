package logopedis.libutils.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic lessonEventsTopic() {
        return TopicBuilder.name("lesson-topic")
                .partitions(3)        // количество partition-ов
                .replicas(1)          // количество реплик
                .build();
    }
}
