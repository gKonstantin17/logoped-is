package logopedis.rsmain.service;

import logopedis.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.rsmain.dto.speechError.SpeechErrorDto;
import logopedis.rsmain.dto.speechError.SpeechErrorReadDto;
import logopedis.rsmain.entity.SpeechError;
import logopedis.rsmain.repository.SpeechErrorRepository;
import logopedis.rsmain.utils.hibernate.ResponseHelper;
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
    public CompletableFuture<ServiceResult<List<SpeechErrorReadDto>>> findall() {
        var data = repository.findAll();
        var result = data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
    }

    @Async
    public CompletableFuture<ServiceResult<SpeechErrorReadDto>> create(SpeechErrorDto dto) {
        try {
            SpeechError speechError = new SpeechError();
            speechError.setTitle(dto.title());
            speechError.setDescription(dto.description());

            SpeechError created = repository.save(speechError);
            var result = toReadDto(created);
            return AsyncResult.success(result);
        } catch(Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }

}

    @Async
    public CompletableFuture<ServiceResult<SpeechErrorReadDto>> update(Long id, SpeechErrorDto dto) {
        try {
            var updated = ResponseHelper.findById(repository,id,"Речевая ошибка не найдена");

            if (dto.title() != null) updated.setTitle(dto.title());
            if (dto.description() != null) updated.setDescription(dto.description());
            repository.save(updated);

            var result = toReadDto(updated);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        try {
            var deletedData = ResponseHelper.findById(repository,id,"Речевая ошибка не найдена");
            repository.deleteById(id);
            return AsyncResult.success(deletedData.getId());
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    private SpeechErrorReadDto toReadDto (SpeechError entity) {
        return new SpeechErrorReadDto(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription()
        );
    }
}
