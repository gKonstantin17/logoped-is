package gk17.rsmain.service;

import gk17.rsmain.dto.patient.PatientCreateDto;
import gk17.rsmain.dto.patient.PatientDto;
import gk17.rsmain.dto.patient.PatientReadDto;
import gk17.rsmain.dto.patient.PatientWithSpeechCard;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.dto.soundCorrection.SoundCorrectionDto;
import gk17.rsmain.dto.speechError.SpeechErrorDto;
import gk17.rsmain.entity.*;
import gk17.rsmain.repository.PatientRepository;
import gk17.rsmain.utils.hibernate.ResponseHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class PatientService {
    private final PatientRepository repository;
    private final UserService userService;
    private final LogopedService logopedService;
    public PatientService(PatientRepository repository, UserService userService, LogopedService logopedService) {
        this.repository = repository;
        this.userService = userService;
        this.logopedService = logopedService;
    }
    @Async
    public CompletableFuture<ServiceResult<List<PatientReadDto>>> findall() {
        var data = repository.findAll();
        List<PatientReadDto> result = data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
    }
    @Async
    public CompletableFuture<ServiceResult<List<PatientWithSpeechCard>>> findAllWithSC(UUID userId) {
        try {
            var data = repository.findAllWithSpeechData(userId);
            List<PatientWithSpeechCard> result = data.stream().map(this::toDtoWithSC).toList();
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }


    public List<Patient> findAllById(List<Long> patientsId) {
        return repository.findAllById(patientsId);
    }
    @Async
    public CompletableFuture<ServiceResult<PatientReadDto>> create(PatientCreateDto dto) {
        try {
            var user = userService.findById(dto.userId()).get();
            Patient patient = new Patient();
            patient.setFirstName(dto.firstName());
            patient.setLastName(dto.lastName());
            patient.setDateOfBirth(dto.dateOfBirth());
            patient.setHidden(false);
            patient.setUser(user);

            Patient createdPatient = repository.save(patient);

            var result = toReadDto(createdPatient);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    public Logoped findLogoped(Long patientId) {
        var patient = repository.findById(patientId);
        return patient.get().getLogoped();
    }

    public void createAll(List<Patient> patients) {
        repository.saveAll(patients);
    }

    @Async
    public CompletableFuture<ServiceResult<List<PatientReadDto>>> findByUserId(UUID userId) {
        try {
            var patients = repository.findByUserId(userId);
            List<PatientReadDto> result = patients.stream().map(this::toReadDto).toList();
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<List<PatientReadDto>>> findByLogopegId(UUID logopegId) {
        try {
            var patients = repository.findByLogopedId(logopegId);
            List<PatientReadDto> result = patients.stream().map(this::toReadDto).toList();
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
            if (dto.lastName() != null)    updated.setLastName(dto.lastName());
            if (dto.dateOfBirth() != null)   updated.setDateOfBirth(dto.dateOfBirth());
            if (dto.userId() != null) {
                var user = userService.findById(dto.userId()).get();
                updated.setUser(user);
            }
            if (dto.logopedId() != null) {
               var logoped = logopedService.findById(dto.logopedId()).get();
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
    public CompletableFuture<ServiceResult<PatientReadDto>> hide(Long id) {
        try {
            var dataForHide = ResponseHelper.findById(repository,id,"Пациент не найден");
            dataForHide.setHidden(true);
            var hiddenData =  toReadDto(repository.save(dataForHide));
            return AsyncResult.success(hiddenData);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<PatientReadDto>> restore(Long id) {
        try {
            var dataForHide = ResponseHelper.findById(repository,id,"Пациент не найден");
            dataForHide.setHidden(false);
            var hiddenData =  toReadDto(repository.save(dataForHide));
            return AsyncResult.success(hiddenData);
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

    @Async
    public CompletableFuture<ServiceResult<Boolean>> existsCardByPatient(Long id) {
        try {
            var result = repository.existsSpeechCardByPatientId(id);
            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    private PatientReadDto toReadDto(Patient entity) {
        return new PatientReadDto(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDateOfBirth(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getLogoped() != null ? entity.getLogoped().getId() : null,
                entity.isHidden()
        );
    }

    private PatientWithSpeechCard toDtoWithSC(Patient patient) {
        Set<SpeechErrorDto> errors = new HashSet<>();
        Set<SoundCorrectionDto> corrections = new HashSet<>();

        for (Lesson lesson : patient.getLessons()) {
            Diagnostic diag = lesson.getDiagnostic();
            if (diag != null && diag.getSpeechCard() != null) {
                SpeechCard sc = diag.getSpeechCard();

                if (sc.getSpeechErrors() != null) {
                    for (SpeechError se : sc.getSpeechErrors()) {
                        errors.add(new SpeechErrorDto(se.getTitle(), se.getDescription()));
                    }
                }

                if (sc.getSoundCorrections() != null) {
                    for (SoundCorrection corr : sc.getSoundCorrections()) {
                        corrections.add(new SoundCorrectionDto(corr.getSound(), corr.getCorrection()));
                    }
                }
            }
        }

        return new PatientWithSpeechCard(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                new ArrayList<>(errors),
                new ArrayList<>(corrections)
        );
    }

}
