package gk17.rsmain.dto.lesson;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record LessonWithHomeworkDto(
        String type,
        String topic,
        String description,
        Timestamp dateOfLesson,
        UUID logopedId,
        String homework,
        List<Long> patientsId
) {}

