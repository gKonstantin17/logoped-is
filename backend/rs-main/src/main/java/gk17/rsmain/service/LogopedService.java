package gk17.rsmain.service;

import gk17.rsmain.dto.logoped.LogopedDto;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.entity.Logoped;
import gk17.rsmain.repository.LogopedRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LogopedService {
    private final LogopedRepository repository;
    public LogopedService(LogopedRepository repository) {
        this.repository = repository;
    }


    @Async
    public CompletableFuture<ServiceResult<List<Logoped>>> findall() {
        var data = repository.findAll();
        return AsyncResult.success(data);
    }

    @Async
    public CompletableFuture<ServiceResult<Logoped>> create(LogopedDto dto) {
        try {
            Logoped logoped = new Logoped();
            logoped.setFirstName(dto.firstName());
            logoped.setSecondName(dto.secondName());
            logoped.setEmail(dto.email());
            logoped.setPhone(dto.phone());

            Logoped result = repository.save(logoped);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<Logoped>> update(Long id, LogopedDto dto) {
        try {
            var data = repository.findById(id);
            if (data.isEmpty())
                return AsyncResult.error("Логопед не найден");
            var result = data.get();

            if (dto.firstName() != null)     result.setFirstName(dto.firstName());
            if (dto.secondName() != null)    result.setSecondName(dto.secondName());
            if (dto.email() != null)         result.setEmail(dto.email());
            if (dto.phone() != null)         result.setPhone(dto.phone());

            return AsyncResult.success(repository.save(result));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<Logoped>> delete(Long id) {
        var result = repository.findById(id);
        if (result.isEmpty())
            return AsyncResult.error("Логопед не найден");

        var deletedData = result.get();
        repository.deleteById(id);
        return AsyncResult.success(deletedData);
    }

}
