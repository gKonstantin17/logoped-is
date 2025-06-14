package gk17.rsmain.dto.lesson;

import java.sql.Timestamp;
import java.util.List;

public record LessonWithHomeworkDto(
        String type,
        String topic,
        String description,
        Timestamp dateOfLesson,
        Long logopedId,
        String homework,
        List<Long> patientsId
) {}

