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
        List<PatientReadDto> result = data.stream().map(patient -> new PatientReadDto(
                patient.getId(),
                patient.getFirstName(),
                patient.getSecondName(),
                patient.getDateOfBirth(),
                patient.getUser() != null ? patient.getUser().getId() : null,
                patient.getLogoped() != null ? patient.getLogoped().getId() : null
        )).toList();
        return AsyncResult.success(result);
    }

    @Async
    public CompletableFuture<ServiceResult<PatientReadDto>> create(PatientCreateDto dto) {
        try {
            var user = userRepository.findById(dto.userId());
            if (user.isEmpty())
                return AsyncResult.error("Пользователь не найден");

            Patient patient = new Patient();
            patient.setFirstName(dto.firstName());
            patient.setSecondName(dto.secondName());
            patient.setDateOfBirth(dto.dateOfBirth());
            patient.setUser(user.get());

            Patient createdPatient = repository.save(patient);

            PatientReadDto result = new PatientReadDto(
                    createdPatient.getId(),
                    createdPatient.getFirstName(),
                    createdPatient.getSecondName(),
                    createdPatient.getDateOfBirth(),
                    patient.getUser() != null ? patient.getUser().getId() : null,
                    patient.getLogoped() != null ? patient.getLogoped().getId() : null
            );
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<PatientReadDto>> update(Long id, PatientDto dto) {
        try {
            var data = repository.findById(id);
            if (data.isEmpty())
                return AsyncResult.error("Пациент не найден");
            var result = data.get();

            if (dto.firstName() != null)     result.setFirstName(dto.firstName());
            if (dto.secondName() != null)    result.setSecondName(dto.secondName());
            if (dto.dateOfBirth() != null)   result.setDateOfBirth(dto.dateOfBirth());
            if (dto.userId() != null) {
                var user = userRepository.findById(dto.userId());
                if (user.isEmpty())
                    return AsyncResult.error("Пользователь не найден");
                result.setUser(user.get());
            }
            if (dto.logopedId() != null) {
                var logoped = logopedRepository.findById(dto.logopedId());
                if (logoped.isEmpty())
                    return AsyncResult.error("Логопед не найден");
                result.setLogoped(logoped.get());
            }

            var updatedPatient  = repository.save(result);
            PatientReadDto resultDto = new PatientReadDto(
                    updatedPatient.getId(),
                    updatedPatient.getFirstName(),
                    updatedPatient.getSecondName(),
                    updatedPatient.getDateOfBirth(),
                    updatedPatient.getUser() != null ? updatedPatient.getUser().getId() : null,
                    updatedPatient.getLogoped() != null ? updatedPatient.getLogoped().getId() : null
            );
            return AsyncResult.success(resultDto);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        var result = repository.findById(id);
        if (result.isEmpty())
            return AsyncResult.error("Пациент не найден");

        var deletedData = result.get();
        repository.deleteById(id);
        return AsyncResult.success(deletedData.getId());
    }
}
