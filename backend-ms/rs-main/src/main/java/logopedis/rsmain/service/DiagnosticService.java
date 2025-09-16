package logopedis.rsmain.service;

import logopedis.libentities.rsmain.dto.diagnostic.DiagnosticDto;
import logopedis.libentities.rsmain.dto.diagnostic.DiagnosticReadDto;
import logopedis.libentities.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.entity.Diagnostic;
import logopedis.rsmain.repository.DiagnosticRepository;
import logopedis.libutils.hibernate.ResponseHelper;
import logopedis.rsmain.repository.SpeechCardRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class DiagnosticService {
    private final DiagnosticRepository repository;
    private final LessonService lessonService;
    private final SpeechCardRepository speechCardRepository;

    public DiagnosticService(DiagnosticRepository repository, LessonService lessonService, SpeechCardRepository speechCardRepository) {
        this.repository = repository;
        this.lessonService = lessonService;
        this.speechCardRepository = speechCardRepository;
    }

    @Async
    public CompletableFuture<ServiceResult<List<DiagnosticReadDto>>> findall() {
        var data = repository.findAll();
        var result = data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
    }
    public Diagnostic findLatestDiagnosticByPatientId(Long patientId) {
        return repository.findLatestDiagnosticByPatientId(patientId).get();
    }
    public Diagnostic findEarliestDiagnosticByPatientId(Long patientId) {
        return repository.findEarliestDiagnosticByPatientId(patientId).get();
    }
    public List<Diagnostic> findAllByPatientId(Long patientId) {
        return repository.findAllByPatientIdWithSpeechCard(patientId);
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
                var speechCard = speechCardRepository.findById(dto.speechCardId())
                        .orElseThrow(() -> new RuntimeException("Речевая карта не найдена"));
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
                var speechCard = speechCardRepository.findById(dto.speechCardId())
                        .orElseThrow(() -> new RuntimeException("Речевая карта не найдена"));
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

    public Diagnostic save(Diagnostic diagnostic) {
        return repository.save(diagnostic);
    }
}
