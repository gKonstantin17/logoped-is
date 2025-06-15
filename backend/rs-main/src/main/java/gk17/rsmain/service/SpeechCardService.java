package gk17.rsmain.service;

import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.dto.speechCard.SCFromDiagnosticDto;
import gk17.rsmain.dto.speechCard.SpeechCardDto;
import gk17.rsmain.dto.speechCard.SpeechCardFullDto;
import gk17.rsmain.dto.speechCard.SpeechCardReadDto;
import gk17.rsmain.entity.*;
import gk17.rsmain.repository.*;
import gk17.rsmain.utils.hibernate.ResponseHelper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class SpeechCardService {
    private final SpeechCardRepository repository;
    private final SoundCorrectionRepository soundCorrectionRepository;
    private final SpeechErrorRepository speechErrorRepository;
    private final LogopedRepository logopedRepository;
    private final LessonRepository lessonRepository;
    private final DiagnosticRepository diagnosticRepository;

    public SpeechCardService(SpeechCardRepository repository, SoundCorrectionRepository soundCorrectionRepository, SpeechErrorRepository speechErrorRepository, LogopedRepository logopedRepository,
                             LessonRepository lessonRepository, DiagnosticRepository diagnosticRepository) {
        this.repository = repository;
        this.soundCorrectionRepository = soundCorrectionRepository;
        this.speechErrorRepository = speechErrorRepository;
        this.logopedRepository = logopedRepository;
        this.lessonRepository = lessonRepository;
        this.diagnosticRepository = diagnosticRepository;
    }

    @Async
    public CompletableFuture<ServiceResult<List<SpeechCardReadDto>>> findall() {
        var data = repository.findAll();
        var result = data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
    }
    @Async
    public CompletableFuture<ServiceResult<SpeechCardFullDto>> findByPatientId(Long patientId) throws ChangeSetPersister.NotFoundException {
        SpeechCard card = repository.findDetailedByPatientId(patientId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        Diagnostic diagnostic = diagnosticRepository.findBySpeechCard(card)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        Lesson lesson = diagnostic.getLesson();
        Logoped logoped = lesson.getLogoped();
        Patient patient = lesson.getPatients()
                .stream()
                .filter(p -> p.getId().equals(patientId))
                .findFirst()
                .orElseThrow();

        SpeechCardFullDto dto = new SpeechCardFullDto(
                card.getId(),
                card.getReason(),
                card.getStateOfHearning(),
                card.getAnamnesis(),
                card.getGeneralMotor(),
                card.getFineMotor(),
                card.getArticulatory(),
                card.getSoundReproduction(),
                card.getSoundComponition(),
                card.getSpeechChars(),
                card.getPatientChars(),
                card.getSpeechErrors().stream().map(SpeechError::getTitle).toList(),
                card.getSoundCorrections().stream()
                        .map(sc -> sc.getSound() + ": " + sc.getCorrection())
                        .toList(),
                diagnostic.getDate(),
                logoped.getFirstName(),
                logoped.getLastName(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth()
        );

        return AsyncResult.success(dto);
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


//            Set<SpeechError> errors = dto.speechErrors() == null
//                    ? Set.of()
//                    : new HashSet<>(speechErrorRepository.findAllById(dto.speechErrors()));
//            Set<SoundCorrection> corrections = dto.soundCorrections() == null
//                    ? Set.of()
//                    : new HashSet<>(soundCorrectionRepository.findAllById(dto.soundCorrections()));

            // контроллер не пропустит null
            Set<SoundCorrection> corrections = new HashSet<>(soundCorrectionRepository.findAllById(dto.soundCorrections()));
            Set<SpeechError> errors = new HashSet<>(speechErrorRepository.findAllById(dto.speechErrors()));
            speechCard.setSpeechErrors(errors);
            speechCard.setSoundCorrections(corrections);

            SpeechCard result = repository.save(speechCard);
            return AsyncResult.success(toReadDto(result));
        } catch(Exception ex){
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<SpeechCardReadDto>> createFromDiag(SCFromDiagnosticDto dto) {
        try {
            // 1. Собираем нарушения
            Set<SpeechError> errors = new HashSet<>(speechErrorRepository.findAllById(dto.speechErrors()));

            // 2. Обрабатываем soundCorrections
            Set<SoundCorrection> corrections = dto.soundCorrections().stream().map(corrDto -> {
                return soundCorrectionRepository.findBySoundAndCorrection(
                        corrDto.sound(), corrDto.correction()
                ).orElseGet(() -> {
                    SoundCorrection newCorrection = new SoundCorrection();
                    newCorrection.setSound(corrDto.sound());
                    newCorrection.setCorrection(corrDto.correction());
                    return soundCorrectionRepository.save(newCorrection);
                });
            }).collect(Collectors.toSet());

            // 3. Создаем речевую карту
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
            speechCard.setSpeechErrors(errors);
            speechCard.setSoundCorrections(corrections);

            SpeechCard savedCard = repository.save(speechCard);

            // 4. Создаем диагностику
            Lesson lesson = lessonRepository.findById(dto.lessonId())
                    .orElseThrow(() -> new RuntimeException("Занятие не найдено"));

            Diagnostic diagnostic = new Diagnostic();
            diagnostic.setSpeechCard(savedCard);
            diagnostic.setLesson(lesson);
            diagnostic.setDate(new Timestamp(System.currentTimeMillis()));
            diagnosticRepository.save(diagnostic);

            return AsyncResult.success(toReadDto(savedCard));

        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }


    @Async
    public CompletableFuture<ServiceResult<SpeechCardReadDto>> update(Long id, SpeechCardDto dto) {
        try {
            var updated = ResponseHelper.findById(repository,id,"Речевая карта не найдена");

            if (dto.reason() != null)           updated.setReason(dto.reason());
            if (dto.stateOfHearning() != null) updated.setStateOfHearning(dto.stateOfHearning());
            if (dto.anamnesis() != null)        updated.setAnamnesis(dto.anamnesis());
            if (dto.generalMotor() != null)     updated.setGeneralMotor(dto.generalMotor());
            if (dto.fineMotor() != null)        updated.setFineMotor(dto.fineMotor());
            if (dto.articulatory() != null)     updated.setArticulatory(dto.articulatory());
            if (dto.soundReproduction() != null) updated.setSoundReproduction(dto.soundReproduction());
            if (dto.soundComponition() != null) updated.setSoundComponition(dto.soundComponition());
            if (dto.speechChars() != null)      updated.setSpeechChars(dto.speechChars());
            if (dto.patientChars() != null)     updated.setPatientChars(dto.patientChars());

            if (dto.speechErrors() != null) {
                var errors = speechErrorRepository.findAllById(dto.speechErrors());
                if (errors.size() != dto.speechErrors().size())
                    return AsyncResult.error("Данный список речевых ошибок не найден");

                updated.setSpeechErrors(new HashSet<>(errors));
            }


            if (dto.soundCorrections() != null) {
                var corrections = soundCorrectionRepository.findAllById(dto.soundCorrections());
                if (corrections.size() != dto.soundCorrections().size())
                    return AsyncResult.error("Данный список направлений коррекции не найден");
                updated.setSoundCorrections(new HashSet<>(corrections));
            }

            var result = repository.save(updated);
            return AsyncResult.success(toReadDto(result));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }


    @Async
    public CompletableFuture<ServiceResult<Long>> delete(Long id) {
        try {
            var deletedData = ResponseHelper.findById(repository,id,"Речевая карта не найдена");
            repository.deleteById(id);
            return AsyncResult.success(deletedData.getId());
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
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
