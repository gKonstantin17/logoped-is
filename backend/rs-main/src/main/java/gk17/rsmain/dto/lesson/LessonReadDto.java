package gk17.rsmain.dto.lesson;

import java.sql.Timestamp;
import java.util.List;

public record LessonReadDto(
        Long Id,
        String type,
        String topic,
        String description,
        Timestamp dateOfLesson,
        Long logopedId,
        Long homeworkId,
        List<Long> patientsId
        ){ }
