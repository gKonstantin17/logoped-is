package logopedis.rsmain.dto.lesson;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record LessonReadDto(
        Long id,
        String type,
        String topic,
        String description,
        Timestamp dateOfLesson,
        String status,
        UUID logopedId,
        Long homeworkId,
        List<Long> patientsId
        ){ }
