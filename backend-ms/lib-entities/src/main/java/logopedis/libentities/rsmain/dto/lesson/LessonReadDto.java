package logopedis.libentities.rsmain.dto.lesson;

import logopedis.libentities.enums.LessonStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record LessonReadDto(
        Long id,
        String type,
        String topic,
        String description,
        Timestamp dateOfLesson,
        LessonStatus status,
        UUID logopedId,
        Long homeworkId,
        List<Long> patientsId
        ){ }
