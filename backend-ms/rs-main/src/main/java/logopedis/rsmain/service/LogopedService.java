package logopedis.rsmain.service;

import logopedis.libentities.rsmain.dto.logoped.LogopedDto;
import logopedis.libentities.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.dto.user.BaseUserDto;
import logopedis.libentities.rsmain.entity.Logoped;
import logopedis.rsmain.repository.LogopedRepository;
import logopedis.rsmain.utils.hibernate.ResponseHelper;
import logopedis.rsmain.utils.keycloak.KeycloakAdminService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class LogopedService {
    private final LogopedRepository repository;
    private final KeycloakAdminService kcService;
    public LogopedService(LogopedRepository repository, KeycloakAdminService kcService) {
        this.repository = repository;
        this.kcService = kcService;
    }


    @Async
    public CompletableFuture<ServiceResult<List<Logoped>>> findall() {
        var data = repository.findAll();
        return AsyncResult.success(data);
    }

    public Optional<Logoped> findById(UUID id) {
        return repository.findById(id);
    }

    @Async
    public CompletableFuture<ServiceResult<Logoped>> create(BaseUserDto dto) {
        try {
            Logoped logoped = logopedFromDto(dto);

            Logoped result = repository.save(logoped);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<Logoped>> update(UUID id, LogopedDto dto) {
        try {
            var updated = ResponseHelper.findById(repository,id,"Логопед не найден");

            if (dto.firstName() != null)     updated.setFirstName(dto.firstName());
            if (dto.lastName() != null)    updated.setLastName(dto.lastName());
            if (dto.email() != null)         updated.setEmail(dto.email());
            if (dto.phone() != null)         updated.setPhone(dto.phone());

            Logoped saved = repository.save(updated);

            // Обновляем Keycloak
            if (saved.getId() != null) {
                kcService.updateUserInKeycloak(saved.getId(), dto);
            }

            return AsyncResult.success(repository.save(saved));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<UUID>> delete(UUID id) {
        try {
            var deletedData = ResponseHelper.findById(repository,id,"Логопед не найден");
            repository.deleteById(id);
            return AsyncResult.success(deletedData.getId());
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    private Logoped logopedFromDto(BaseUserDto dto) {
        Logoped logoped = new Logoped();
        logoped.setFirstName(dto.firstName());
        logoped.setLastName(dto.lastName());
        logoped.setEmail(dto.email());
        logoped.setPhone(dto.phone());
        return logoped;
    }



}
