package gk17.rsmain.service;

import gk17.rsmain.dto.diagnostic.DiagnosticDto;
import gk17.rsmain.dto.homework.HomeworkDto;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.entity.Diagnostic;
import gk17.rsmain.entity.Homework;
import gk17.rsmain.entity.UserData;
import gk17.rsmain.repository.DiagnosticRepository;
import gk17.rsmain.repository.HomeworkRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class HomeworkService {
    private final HomeworkRepository repository;

    public HomeworkService(HomeworkRepository repository) {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<ServiceResult<List<Homework>>> findall() {
        var data = repository.findAll();
        return AsyncResult.success(data);
    }

    @Async
    public CompletableFuture<ServiceResult<Homework>> create(HomeworkDto dto) {
        try {
            Homework homework = new Homework();
            homework.setTask(dto.task());

            Homework result = repository.save(homework);
            return AsyncResult.success(result);
        } catch(Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }

    }
    @Async
    public CompletableFuture<ServiceResult<Homework>> update(Long id, HomeworkDto dto) {
        try {
            var data = repository.findById(id);
            if (data.isEmpty())
                return AsyncResult.error("Домашняя работа не найдена");
            var result = data.get();

            if (dto.task() != null) result.setTask(dto.task());

            return AsyncResult.success(repository.save(result));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<Homework>> delete(Long id) {
        var result = repository.findById(id);
        if (result.isEmpty())
            return AsyncResult.error("Домашняя работа не найден");

        var deletedData = result.get();
        repository.deleteById(id);
        return AsyncResult.success(deletedData);
    }
}
