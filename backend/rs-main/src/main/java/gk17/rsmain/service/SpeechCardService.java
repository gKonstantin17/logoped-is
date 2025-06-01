package gk17.rsmain.service;

import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.dto.speechCard.SpeechCardDto;
import gk17.rsmain.dto.speechCard.SpeechCardReadDto;
import gk17.rsmain.entity.Patient;
import gk17.rsmain.entity.SoundCorrection;
import gk17.rsmain.entity.SpeechCard;
import gk17.rsmain.entity.SpeechError;
import gk17.rsmain.repository.SoundCorrectionRepository;
import gk17.rsmain.repository.SpeechCardRepository;
import gk17.rsmain.repository.SpeechErrorRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class SpeechCardService {
    private final SpeechCardRepository repository;
    private final SoundCorrectionRepository soundCorrectionRepository;
    private final SpeechErrorRepository speechErrorRepository;

    public SpeechCardService(SpeechCardRepository repository, SoundCorrectionRepository soundCorrectionRepository, SpeechErrorRepository speechErrorRepository) {
        this.repository = repository;
        this.soundCorrectionRepository = soundCorrectionRepository;
        this.speechErrorRepository = speechErrorRepository;
    }

    @Async
    public CompletableFuture<ServiceResult<List<SpeechCardReadDto>>> findall() {
        var data = repository.findAll();
        var result = data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
    }


    @Async
    public CompletableFuture<ServiceResult<SpeechCardReadDto>> create(SpeechCardDto dto) {
        try {
            SpeechCard speechCard = new SpeechCard();
            speechCard.setReason(dto.reason());
            speechCard.setStateOfHearning(dto.stateOfHearning());
            speechCard.setAnamnesis(dto.anamnesis());
            speechCard.setGeneralMotor(dto.generalMotor());
            speechCard.setFineMotor(dto.fineMotor());
            speechCard.setArticulatory(dto.articulatory());
            speechCard.setSoundReproduction(dto.soundReproduction());
            speechCard.setSoundComponition(dto.soundComponition());
            speechCard.setSpeechChars(dto.speechChars());
            speechCard.setPatientChars(dto.patientChars());


            Set<SpeechError> errors = dto.speechErrors() == null
                    ? Set.of()
                    : new HashSet<>(speechErrorRepository.findAllById(dto.speechErrors()));
            Set<SoundCorrection> corrections = dto.soundCorrections() == null
                    ? Set.of()
                    : new HashSet<>(soundCorrectionRepository.findAllById(dto.soundCorrections()));
            speechCard.setSpeechErrors(errors);
            speechCard.setSoundCorrections(corrections);

            SpeechCard result = repository.save(speechCard);
            return AsyncResult.success(toReadDto(result));
        } catch(Exception ex){
            return AsyncResult.error(ex.getMessage());
        }
    }


    @Async
    public CompletableFuture<ServiceResult<SpeechCardReadDto>> update(Long id, SpeechCardDto dto) {
        try {
            var data = repository.findById(id);
            if (data.isEmpty())
                return AsyncResult.error("Речевая карта не найдена");

            var speechCard = data.get();

            if (dto.reason() != null)           speechCard.setReason(dto.reason());
            if (dto.stateOfHearning() != null) speechCard.setStateOfHearning(dto.stateOfHearning());
            if (dto.anamnesis() != null)        speechCard.setAnamnesis(dto.anamnesis());
            if (dto.generalMotor() != null)     speechCard.setGeneralMotor(dto.generalMotor());
            if (dto.fineMotor() != null)        speechCard.setFineMotor(dto.fineMotor());
            if (dto.articulatory() != null)     speechCard.setArticulatory(dto.articulatory());
            if (dto.soundReproduction() != null) speechCard.setSoundReproduction(dto.soundReproduction());
            if (dto.soundComponition() != null) speechCard.setSoundComponition(dto.soundComponition());
            if (dto.speechChars() != null)      speechCard.setSpeechChars(dto.speechChars());
            if (dto.patientChars() != null)     speechCard.setPatientChars(dto.patientChars());

            if (dto.speechErrors() != null) {
                var errors = speechErrorRepository.findAllById(dto.speechErrors());
                if (errors.size() != dto.speechErrors().size())
                    return AsyncResult.error("Данный список речевых ошибок не найден");

                speechCard.setSpeechErrors(new HashSet<>(errors));
            }


            if (dto.soundCorrections() != null) {
                var corrections = soundCorrectionRepository.findAllById(dto.soundCorrections());
                if (corrections.size() != dto.soundCorrections().size())
                    return AsyncResult.error("Данный список направлений коррекции не найден");
                speechCard.setSoundCorrections(new HashSet<>(corrections));
            }

            var updated = repository.save(speechCard);
            return AsyncResult.success(toReadDto(updated));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }


    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        var result = repository.findById(id);
        if (result.isEmpty())
            return AsyncResult.error("Речевая карта не найдена");

        var deletedData = result.get();
        repository.deleteById(id);
        return AsyncResult.success(deletedData.getId());
    }

    private SpeechCardReadDto toReadDto(SpeechCard entity) {
        return new SpeechCardReadDto(
                entity.getId(),
                entity.getReason(),
                entity.getStateOfHearning(),
                entity.getAnamnesis(),
                entity.getGeneralMotor(),
                entity.getFineMotor(),
                entity.getArticulatory(),
                entity.getSoundReproduction(),
                entity.getSoundComponition(),
                entity.getSpeechChars(),
                entity.getPatientChars(),
                entity.getSpeechErrors().stream().map(SpeechError::getId).toList(),
                entity.getSoundCorrections().stream().map(SoundCorrection::getId).toList()
        );
    }

}
