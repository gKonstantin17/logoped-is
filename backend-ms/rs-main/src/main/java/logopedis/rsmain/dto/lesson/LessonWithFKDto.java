package logopedis.rsmain.dto.lesson;

import logopedis.rsmain.dto.homework.HomeworkDto;
import logopedis.rsmain.dto.logoped.LogopedDto;
import logopedis.rsmain.dto.patient.PatientWithoutFKDto;

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
