package gk17.rsmain.service;

import gk17.rsmain.dto.homework.HomeworkDto;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.entity.Homework;
import gk17.rsmain.repository.HomeworkRepository;
import gk17.rsmain.utils.hibernate.ResponseHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    public Optional<Homework> findById(Long id) {
        return repository.findById(id);
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
    public Homework create(String task) {
        Homework homework = new Homework();
        homework.setTask(task);
        return repository.save(homework);
    }
    @Async
    public CompletableFuture<ServiceResult<Homework>> update(Long id, HomeworkDto dto) {
        try {
            var updated = ResponseHelper.findById(repository,id,"Домашняя работа не найдена");
            if (dto.task() != null) updated.setTask(dto.task());
            return AsyncResult.success(repository.save(updated));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        try {
            var deletedData = ResponseHelper.findById(repository,id,"Домашняя работа не найдена");
            repository.deleteById(id);
            return AsyncResult.success(deletedData.getId());
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }

    }
}
