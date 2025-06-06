package gk17.rsmain.service;

import gk17.rsmain.dto.patient.PatientCreateDto;
import gk17.rsmain.dto.patient.PatientDto;
import gk17.rsmain.dto.patient.PatientReadDto;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.entity.Patient;
import gk17.rsmain.repository.LogopedRepository;
import gk17.rsmain.repository.PatientRepository;
import gk17.rsmain.repository.UserRepository;
import gk17.rsmain.utils.hibernate.ResponseHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PatientService {
    private final PatientRepository repository;
    private final UserRepository userRepository;
    private final LogopedRepository logopedRepository;

    public PatientService(PatientRepository repository, UserRepository userRepository, LogopedRepository logopedRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.logopedRepository = logopedRepository;
    }
    @Async
    public CompletableFuture<ServiceResult<List<PatientReadDto>>> findall() {
        var data = repository.findAll();
        List<PatientReadDto> result = data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
    }

    @Async
    public CompletableFuture<ServiceResult<PatientReadDto>> create(PatientCreateDto dto) {
        try {
            var user = ResponseHelper.findById(userRepository,dto.userId(),"Пользователь не найден");

            Patient patient = new Patient();
            patient.setFirstName(dto.firstName());
            patient.setSecondName(dto.secondName());
            patient.setDateOfBirth(dto.dateOfBirth());
            patient.setUser(user);

            Patient createdPatient = repository.save(patient);

            var result = toReadDto(createdPatient);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<PatientReadDto>> update(Long id, PatientDto dto) {
        try {
            var updated = ResponseHelper.findById(repository,id,"Пациент не найден");

            if (dto.firstName() != null)     updated.setFirstName(dto.firstName());
            if (dto.secondName() != null)    updated.setSecondName(dto.secondName());
            if (dto.dateOfBirth() != null)   updated.setDateOfBirth(dto.dateOfBirth());
            if (dto.userId() != null) {
                var user = ResponseHelper.findById(userRepository,dto.userId(),"Пользователь не найден");
                updated.setUser(user);
            }
            if (dto.logopedId() != null) {
                var logoped = ResponseHelper.findById(logopedRepository,dto.logopedId(),"Логопед не найден");
                updated.setLogoped(logoped);
            }

            var updatedPatient  = repository.save(updated);
            var result = toReadDto(updatedPatient);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        try {
            var deletedData = ResponseHelper.findById(repository,id,"Пациент не найден");
            repository.deleteById(id);
            return AsyncResult.success(deletedData.getId());
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    private PatientReadDto toReadDto(Patient entity) {
        return new PatientReadDto(
                entity.getId(),
                entity.getFirstName(),
                entity.getSecondName(),
                entity.getDateOfBirth(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getLogoped() != null ? entity.getLogoped().getId() : null
        );
    }
}
