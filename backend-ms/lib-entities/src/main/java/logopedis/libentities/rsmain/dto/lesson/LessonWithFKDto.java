package logopedis.libentities.rsmain.dto.lesson;

import logopedis.libentities.rsmain.dto.homework.HomeworkDto;
import logopedis.libentities.rsmain.dto.logoped.LogopedDto;
import logopedis.libentities.rsmain.dto.patient.PatientWithoutFKDto;

import java.sql.Timestamp;
import java.util.List;

public record LessonWithFKDto(
        Long id,
        String type,
        String topic,
        String description,
        Timestamp dateOfLesson,
        String status,
        LogopedDto logoped,
        HomeworkDto homework,
        List<PatientWithoutFKDto> patients
) {}
