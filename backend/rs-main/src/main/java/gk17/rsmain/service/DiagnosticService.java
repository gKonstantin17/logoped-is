package gk17.rsmain.service;

import gk17.rsmain.dto.diagnostic.DiagnosticDto;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.entity.Diagnostic;
import gk17.rsmain.repository.DiagnosticRepository;
import gk17.rsmain.repository.LessonRepository;
import gk17.rsmain.repository.SpeechCardRepository;
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
    public CompletableFuture<ServiceResult<List<Diagnostic>>> findall() {
        var data = repository.findAll();
        return AsyncResult.success(data);
    }

    @Async
    public CompletableFuture<ServiceResult<Diagnostic>> create(DiagnosticDto dto) {
        try {
            Diagnostic diagnostic = new Diagnostic();
            diagnostic.setDate(dto.date());

            if (dto.lessonId() != null) {
                var lesson = lessonRepository.findById(dto.lessonId());
                if (lesson.isEmpty())
                    return AsyncResult.error("Урок не найден");
                diagnostic.setLesson(lesson.get());
            }
            if (dto.speechCardId() != null) {
                var speechCard = speechCardRepository.findById(dto.speechCardId());
                if (speechCard.isEmpty())
                    return AsyncResult.error("Речевая карта не найдена");
                diagnostic.setSpeechCard(speechCard.get());
            }

            Diagnostic result = repository.save(diagnostic);
            return AsyncResult.success(result);
        } catch(Exception ex) {
        return AsyncResult.error(ex.getMessage());
    }

}
    @Async
    public CompletableFuture<ServiceResult<Diagnostic>> update(Long id, DiagnosticDto dto) {
        try {
            var data = repository.findById(id);
            if (data.isEmpty())
                return AsyncResult.error("Логопед не найден");
            var result = data.get();

            if (dto.date() != null) result.setDate(dto.date());
            if (dto.lessonId() != null) {
                var lesson = lessonRepository.findById(dto.lessonId());
                if (lesson.isEmpty())
                    return AsyncResult.error("Урок не найден");
                result.setLesson(lesson.get());
            }
            if (dto.speechCardId() != null) {
                var speechCard = speechCardRepository.findById(dto.speechCardId());
                if (speechCard.isEmpty())
                    return AsyncResult.error("Речевая карта не найдена");
                result.setSpeechCard(speechCard.get());
            }

            return AsyncResult.success(repository.save(result));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<Diagnostic>> delete(Long id) {
        var result = repository.findById(id);
        if (result.isEmpty())
            return AsyncResult.error("Логопед не найден");

        var deletedData = result.get();
        repository.deleteById(id);
        return AsyncResult.success(deletedData);
    }
}
