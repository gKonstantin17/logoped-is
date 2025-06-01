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

            Logoped savedLogoped = repository.save(logoped);
            return AsyncResult.success(savedLogoped);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<Logoped>> update(Long id, LogopedDto dto) {
        try {
            var result = repository.findById(id);
            if (result.isEmpty())
                return AsyncResult.error("Логопед не найден");
            var logoped = result.get();

            if (dto.firstName() != null)     logoped.setFirstName(dto.firstName());
            if (dto.secondName() != null)    logoped.setSecondName(dto.secondName());
            if (dto.email() != null)         logoped.setEmail(dto.email());
            if (dto.phone() != null)         logoped.setPhone(dto.phone());

            return AsyncResult.success(repository.save(logoped));
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
