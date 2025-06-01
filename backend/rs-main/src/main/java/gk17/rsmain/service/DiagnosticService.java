package gk17.rsmain.service;

import gk17.rsmain.dto.diagnostic.DiagnosticDto;
import gk17.rsmain.dto.diagnostic.DiagnosticReadDto;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.entity.Diagnostic;
import gk17.rsmain.repository.DiagnosticRepository;
import gk17.rsmain.repository.LessonRepository;
import gk17.rsmain.repository.SpeechCardRepository;
import gk17.rsmain.utils.hibernate.ResponseHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class DiagnosticService {
    private final DiagnosticRepository repository;
    private final LessonRepository lessonRepository;
    private final SpeechCardRepository speechCardRepository;

    public DiagnosticService(DiagnosticRepository repository, LessonRepository lessonRepository, SpeechCardRepository speechCardRepository) {
        this.repository = repository;
        this.lessonRepository = lessonRepository;
        this.speechCardRepository = speechCardRepository;
    }

    @Async
    public CompletableFuture<ServiceResult<List<DiagnosticReadDto>>> findall() {
        var data = repository.findAll();
        var result = data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
    }

    @Async
    public CompletableFuture<ServiceResult<DiagnosticReadDto>> create(DiagnosticDto dto) {
        try {
            Diagnostic diagnostic = new Diagnostic();
            diagnostic.setDate(dto.date());

            if (dto.lessonId() != null) {
                var lesson = ResponseHelper.findById(lessonRepository,dto.lessonId(),"Урок не найден");
                diagnostic.setLesson(lesson);
            }
            if (dto.speechCardId() != null) {
                var speechCard = ResponseHelper.findById(speechCardRepository,dto.speechCardId(),"Речевая карта не найдена");
                diagnostic.setSpeechCard(speechCard);
            }

            Diagnostic created = repository.save(diagnostic);
            var result = toReadDto(created);
            return AsyncResult.success(result);
        } catch(Exception ex) {
        return AsyncResult.error(ex.getMessage());
    }

}
    @Async
    public CompletableFuture<ServiceResult<DiagnosticReadDto>> update(Long id, DiagnosticDto dto) {
        try {
            var updated = ResponseHelper.findById(repository,id,"Диагностика не найдена");

            if (dto.date() != null) updated.setDate(dto.date());

            if (dto.lessonId() != null) {
                var lesson = ResponseHelper.findById(lessonRepository,dto.lessonId(),"Урок не найден");
                updated.setLesson(lesson);
            }
            if (dto.speechCardId() != null) {
                var speechCard = ResponseHelper.findById(speechCardRepository,dto.speechCardId(),"Речевая карта не найдена");
                updated.setSpeechCard(speechCard);
            }
            repository.save(updated);
            var result = toReadDto(repository.findById(updated.getId()).get());
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        try {
            var deletedData = ResponseHelper.findById(repository,id,"Диагностика не найдена");
            repository.deleteById(id);
            return AsyncResult.success(deletedData.getId());
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    private DiagnosticReadDto toReadDto (Diagnostic entity) {
        return new DiagnosticReadDto(
                entity.getId(),
                entity.getDate(),
                entity.getLesson().getId(),
                entity.getSpeechCard().getId()
        );
    }
}
