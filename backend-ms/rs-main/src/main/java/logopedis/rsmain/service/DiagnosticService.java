package logopedis.rsmain.service;

import logopedis.libentities.rsmain.dto.diagnostic.DiagnosticDto;
import logopedis.libentities.rsmain.dto.diagnostic.DiagnosticReadDto;
import logopedis.libentities.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.entity.Diagnostic;
import logopedis.rsmain.repository.DiagnosticRepository;
import logopedis.libutils.hibernate.ResponseHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class DiagnosticService {
    private final DiagnosticRepository repository;
    private final LessonService lessonService;
    private final SpeechCardService speechCardService;

    public DiagnosticService(DiagnosticRepository repository, LessonService lessonService, SpeechCardService speechCardService) {
        this.repository = repository;
        this.lessonService = lessonService;
        this.speechCardService = speechCardService;
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
                var lesson = lessonService.findById(dto.lessonId());
                diagnostic.setLesson(lesson);
            }
            if (dto.speechCardId() != null) {
                var speechCard = speechCardService.findById(dto.speechCardId());
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
                var lesson = lessonService.findById(dto.lessonId());
                updated.setLesson(lesson);
            }
            if (dto.speechCardId() != null) {
                var speechCard = speechCardService.findById(dto.speechCardId());
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
