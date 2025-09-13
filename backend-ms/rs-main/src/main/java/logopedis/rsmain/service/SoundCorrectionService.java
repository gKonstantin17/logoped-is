package logopedis.rsmain.service;

import logopedis.libentities.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.dto.soundCorrection.SoundCorrectionChangesDto;
import logopedis.libentities.rsmain.dto.soundCorrection.SoundCorrectionDto;
import logopedis.libentities.rsmain.dto.soundCorrection.SoundCorrectionReadDto;
import logopedis.libentities.rsmain.entity.Diagnostic;
import logopedis.libentities.rsmain.entity.Patient;
import logopedis.libentities.rsmain.entity.SoundCorrection;
import logopedis.rsmain.repository.DiagnosticRepository;
import logopedis.rsmain.repository.SoundCorrectionRepository;
import logopedis.libutils.hibernate.ResponseHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class SoundCorrectionService {
    private final SoundCorrectionRepository repository;
    private final DiagnosticRepository diagnosticRepository;

    public SoundCorrectionService(SoundCorrectionRepository repository,
                                  DiagnosticRepository diagnosticRepository) {
        this.repository = repository;
        this.diagnosticRepository = diagnosticRepository;
    }

    @Async
    public CompletableFuture<ServiceResult<List<SoundCorrectionReadDto>>> findall() {
        var data = repository.findAll();
        var result = data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
    }

    @Async
    public CompletableFuture<ServiceResult<List<SoundCorrectionReadDto>>> findLatestByPatientId(Long patientId) {
        try {
            Set<SoundCorrection> corrections = repository
                    .findLatestSoundCorrectionsByPatientId(patientId)
                    .orElse(Set.of());

            List<SoundCorrectionReadDto> result = corrections.stream()
                    .map(this::toReadDto)
                    .toList();

            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<SoundCorrectionChangesDto>> findChanges(Long lessonId) {
        try {
            Diagnostic current = diagnosticRepository.findByLessonId(lessonId)
                    .orElseThrow(() -> new IllegalArgumentException("Diagnostic not found for lesson " + lessonId));

            Long patientId = current.getLesson()
                    .getPatients()
                    .stream()
                    .findFirst()
                    .map(Patient::getId)
                    .orElseThrow(() -> new IllegalStateException("Lesson has no patients"));

            List<Diagnostic> previousList = diagnosticRepository.findPreviousByPatientIdAndDate(patientId, current.getDate());

            if (previousList.isEmpty()) {
                return AsyncResult.success(new SoundCorrectionChangesDto(Set.of(), Set.of()));
            }

            Diagnostic previous = previousList.get(0);

            Set<SoundCorrection> latest = current.getSpeechCard().getSoundCorrections();
            Set<SoundCorrection> before = previous.getSpeechCard().getSoundCorrections();

            Set<SoundCorrectionReadDto> added = latest.stream()
                    .filter(sc -> !before.contains(sc))
                    .map(this::toReadDto)
                    .collect(Collectors.toSet());

            Set<SoundCorrectionReadDto> removed = before.stream()
                    .filter(sc -> !latest.contains(sc))
                    .map(this::toReadDto)
                    .collect(Collectors.toSet());

            SoundCorrectionChangesDto result = new SoundCorrectionChangesDto(added, removed);

            return AsyncResult.success(result);
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<SoundCorrectionReadDto>> create(SoundCorrectionDto dto) {
        try {
            SoundCorrection soundCorrection = new SoundCorrection();
            soundCorrection.setSound(dto.sound());
            soundCorrection.setCorrection(dto.correction());

            SoundCorrection created = repository.save(soundCorrection);
            var result = toReadDto(created);
            return AsyncResult.success(result);
        } catch( Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }

}
    @Async
    public CompletableFuture<ServiceResult<SoundCorrectionReadDto>> update(Long id, SoundCorrectionDto dto) {
        try {
            var updated = ResponseHelper.findById(repository,id,"Направление коррекции не найдено");

            if (dto.sound() != null) updated.setSound(dto.sound());
            if (dto.correction() != null) updated.setCorrection(dto.correction());
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
            var deletedData = ResponseHelper.findById(repository,id,"Направление коррекции не найдено");
            repository.deleteById(id);
            return AsyncResult.success(deletedData.getId());
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    private SoundCorrectionReadDto toReadDto (SoundCorrection entiry) {
        return new SoundCorrectionReadDto(
          entiry.getId(),
          entiry.getSound(),
          entiry.getCorrection()
        );
    }
}
