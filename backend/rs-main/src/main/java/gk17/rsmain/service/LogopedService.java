package gk17.rsmain.service;

import gk17.rsmain.dto.logoped.LogopedDto;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.entity.Logoped;
import gk17.rsmain.repository.LogopedRepository;
import gk17.rsmain.utils.hibernate.ResponseHelper;
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
            var updated = ResponseHelper.findById(repository,id,"Логопед не найден");

            if (dto.firstName() != null)     updated.setFirstName(dto.firstName());
            if (dto.secondName() != null)    updated.setSecondName(dto.secondName());
            if (dto.email() != null)         updated.setEmail(dto.email());
            if (dto.phone() != null)         updated.setPhone(dto.phone());

            return AsyncResult.success(repository.save(updated));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        try {
            var deletedData = ResponseHelper.findById(repository,id,"Логопед не найден");
            repository.deleteById(id);
            return AsyncResult.success(deletedData.getId());
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

}
