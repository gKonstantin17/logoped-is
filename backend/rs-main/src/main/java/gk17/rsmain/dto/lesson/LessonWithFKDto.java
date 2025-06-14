package gk17.rsmain.dto.lesson;

import gk17.rsmain.dto.homework.HomeworkDto;
import gk17.rsmain.dto.logoped.LogopedDto;
import gk17.rsmain.dto.patient.PatientDto;
import gk17.rsmain.dto.patient.PatientWithoutFK;

import java.sql.Timestamp;
import java.util.List;

public record LessonWithFKDto(
        Long id,
        String type,
        String topic,
        String description,
        Timestamp dateOfLesson,
        LogopedDto logoped,
        HomeworkDto homework,
        List<PatientWithoutFK> patients
) {}
