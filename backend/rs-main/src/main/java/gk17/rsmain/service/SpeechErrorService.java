package gk17.rsmain.service;

import gk17.rsmain.dto.diagnostic.DiagnosticDto;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.dto.speechError.SpeechErrorDto;
import gk17.rsmain.entity.Diagnostic;
import gk17.rsmain.entity.SpeechError;
import gk17.rsmain.entity.UserData;
import gk17.rsmain.repository.DiagnosticRepository;
import gk17.rsmain.repository.SpeechErrorRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class SpeechErrorService {
    private final SpeechErrorRepository repository;

    public SpeechErrorService(SpeechErrorRepository repository) {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<ServiceResult<List<SpeechError>>> findall() {
        var data = repository.findAll();
        return AsyncResult.success(data);
    }

    @Async
    public CompletableFuture<ServiceResult<SpeechError>> create(SpeechErrorDto dto) {
        try {
            SpeechError speechError = new SpeechError();
            speechError.setTitle(dto.title());
            speechError.setDescription(dto.description());

            SpeechError result = repository.save(speechError);
            return AsyncResult.success(result);
        } catch(Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }

}

    @Async
    public CompletableFuture<ServiceResult<SpeechError>> update(Long id, SpeechErrorDto dto) {
        try {
            var data = repository.findById(id);
            if (data.isEmpty())
                return AsyncResult.error("Речевая ошибка не найдена");
            var result = data.get();

            if (dto.title() != null) result.setTitle(dto.title());
            if (dto.description() != null) result.setDescription(dto.description());

            return AsyncResult.success(repository.save(result));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<SpeechError>> delete(Long id) {
        var result = repository.findById(id);
        if (result.isEmpty())
            return AsyncResult.error("Речевая ошибка не найден");

        var deletedData = result.get();
        repository.deleteById(id);
        return AsyncResult.success(deletedData);
    }
}
