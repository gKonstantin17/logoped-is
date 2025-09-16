package logopedis.rsmain.service;

import logopedis.libentities.enums.DiagnosticTypes;
import logopedis.libentities.rsmain.dto.patient.PatientReadDto;
import logopedis.libentities.rsmain.dto.responseWrapper.AsyncResult;
import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.dto.soundCorrection.SoundCorrectionDto;
import logopedis.libentities.rsmain.dto.speechCard.*;
import logopedis.libentities.rsmain.entity.*;
import logopedis.rsmain.repository.*;
import logopedis.libutils.hibernate.ResponseHelper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class SpeechCardService {
    private final SpeechCardRepository repository;
    private final SoundCorrectionRepository soundCorrectionRepository;
    private final SpeechErrorRepository speechErrorRepository;
    private final LessonRepository lessonRepository;
    private final PatientService patientService;
    private final DiagnosticService diagnosticService;


    public SpeechCardService(SpeechCardRepository repository, SoundCorrectionRepository soundCorrectionRepository, SpeechErrorRepository speechErrorRepository,
                             LessonRepository lessonRepository, PatientService patientService, DiagnosticService diagnosticService) {
        this.repository = repository;
        this.soundCorrectionRepository = soundCorrectionRepository;
        this.speechErrorRepository = speechErrorRepository;
        this.lessonRepository = lessonRepository;
        this.patientService = patientService;
        this.diagnosticService = diagnosticService;
    }

    @Async
    public CompletableFuture<ServiceResult<List<SpeechCardReadDto>>> findall() {
        var data = repository.findAll();
        var result = data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
    }
    public SpeechCard findById(Long id) {
        return repository.findById(id).get();
    }
    @Async
    public CompletableFuture<ServiceResult<SpeechCardFullDto>> findByPatientId(Long patientId) throws ChangeSetPersister.NotFoundException {
        SpeechCard card = repository.findLatestSpeechCardByPatientId(patientId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        Diagnostic diagnostic = diagnosticService.findLatestDiagnosticByPatientId(patientId);
        Lesson lesson = diagnostic.getLesson();
        Logoped logoped = lesson.getLogoped();
        Patient patient = patientService.findById(patientId);

        SpeechCardFullDto dto = toFullDto(card,diagnostic,logoped,patient);
        return AsyncResult.success(dto);
    }
    @Async
    public CompletableFuture<ServiceResult<SpeechCardFullDto>> findFullById(Long speechCardId) throws ChangeSetPersister.NotFoundException {
        SpeechCard card = repository.findById(speechCardId).get();

        Diagnostic diagnostic = diagnosticService.findBySpeechCard(card);
        Lesson lesson = diagnostic.getLesson();
        Logoped logoped = lesson.getLogoped();
        Patient patient = lesson.getPatients().stream().findFirst()
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        SpeechCardFullDto dto = toFullDto(card,diagnostic,logoped,patient);
        return AsyncResult.success(dto);
    }

    @Async
    public CompletableFuture<ServiceResult<List<SpechCardMinDto>>> findAllPatientsFirstCards(UUID logopedId)
            throws ChangeSetPersister.NotFoundException, ExecutionException, InterruptedException {

        // Получаем всех пациентов логопеда
        List<PatientReadDto> patients = patientService.findByLogopegId(logopedId).get().data();

        List<SpechCardMinDto> result = new ArrayList<>();

        for (PatientReadDto patientDto : patients) {
            Long patientId = patientDto.id();

            // Получаем первую речевую карту
            Optional<SpeechCard> cardOpt = repository.findEarliestSpeechCardByPatientId(patientId);
            if (cardOpt.isEmpty()) {
                continue; // если карта не найдена — пропускаем пациента
            }
            SpeechCard card = cardOpt.get();

            Diagnostic diagnostic = diagnosticService.findEarliestDiagnosticByPatientId(patientId);
            Patient patient = patientService.findById(patientId);

            SpechCardMinDto dto = new SpechCardMinDto(
                    patient.getId(),
                    patient.getFirstName() + " " + patient.getLastName(),
                    diagnostic.getDate(),
                    card.getSpeechErrors().stream()
                            .map(SpeechError::getTitle)
                            .toList(),
                    card.getSoundCorrections().stream()
                            .map(sc -> sc.getSound() + ": " + sc.getCorrection())
                            .toList(),
                    card.getId()
            );

            result.add(dto);
        }

        return AsyncResult.success(result);
    }



    @Async
    public CompletableFuture<ServiceResult<SpeechCardReadDto>> create(SpeechCardDto dto) {
        try {
            SpeechCard speechCard = SpeechCardFromDto(dto);

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
            SpeechCard speechCard = SpeechCardFromDto(dto);
            speechCard.setSpeechErrors(errors);
            speechCard.setSoundCorrections(corrections);

            SpeechCard savedCard = repository.save(speechCard);

            // 4. Создаем диагностику
            Lesson lesson = lessonRepository.findById(dto.lessonId())
                    .orElseThrow(() -> new RuntimeException("Занятие не найдено"));

            Diagnostic diagnostic = new Diagnostic();
            diagnostic.setSpeechCard(savedCard);
            diagnostic.setLesson(lesson);
            diagnostic.setType(DiagnosticTypes.BEGIN.getDescription());
            diagnostic.setDate(new Timestamp(System.currentTimeMillis()));
            diagnosticService.save(diagnostic);

            return AsyncResult.success(toReadDto(savedCard));

        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<SpeechCardFullDto>> createUpdateWithCorrctions(
            SpeechCardCorrectionDto correctionDto
    ) {
        try {
            Long patientId = correctionDto.patientId();
            List<SoundCorrectionDto> updatedCorrections = correctionDto.updatedCorrections();
            Long lessonId = correctionDto.lessonId();

            SpeechCard lastCard = repository.findLatestSpeechCardByPatientId(patientId)
                    .orElseThrow(() -> new RuntimeException("Речевая карта не найдена"));


            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Занятие не найдено"));
            Patient patient = patientService.findById(patientId);

            Logoped logoped = lesson.getLogoped();


            SpeechCard newCard = SpeechCardFromDto(lastCard);


            Set<SoundCorrection> corrections = updatedCorrections.stream()
                    .map(corrDto -> soundCorrectionRepository.findBySoundAndCorrection(
                            corrDto.sound(), corrDto.correction()
                    ).orElseGet(() -> {
                        SoundCorrection sc = new SoundCorrection();
                        sc.setSound(corrDto.sound());
                        sc.setCorrection(corrDto.correction());
                        return soundCorrectionRepository.save(sc);
                    }))
                    .collect(Collectors.toSet());

            newCard.setSoundCorrections(new HashSet<>(corrections));
            newCard.setSpeechErrors(new HashSet<>(lastCard.getSpeechErrors()));


            SpeechCard savedCard = repository.save(newCard);


            Diagnostic newDiag = new Diagnostic();
            newDiag.setSpeechCard(savedCard);
            newDiag.setLesson(lesson);
            newDiag.setType(DiagnosticTypes.AFTER_LESSON.getDescription());
            newDiag.setDate(new Timestamp(System.currentTimeMillis()));
            diagnosticService.save(newDiag);


            SpeechCardFullDto dto = toFullDto(savedCard, newDiag, logoped, patient);
            return AsyncResult.success(dto);

        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }

    @Async
    public CompletableFuture<ServiceResult<List<PatientHistoryDto>>> findPatientHistory(Long patientId) {
        try {
            List<Diagnostic> diagnostics = diagnosticService.findAllByPatientId(patientId);

            List<PatientHistoryDto> history = new ArrayList<>();
            Set<String> prevErrors = new HashSet<>();
            Set<String> prevCorrections = new HashSet<>();

            for (Diagnostic d : diagnostics.stream().sorted((a,b) -> a.getDate().compareTo(b.getDate())).toList()) {
                SpeechCard sc = d.getSpeechCard();
                Set<String> currErrors = sc.getSpeechErrors().stream()
                        .map(SpeechError::getTitle)
                        .collect(Collectors.toSet());
                Set<String> currCorrections = sc.getSoundCorrections().stream()
                        .map(c -> c.getSound() + ": " + c.getCorrection())
                        .collect(Collectors.toSet());

                // оставляем только изменения относительно предыдущей даты
                Set<String> newErrors = new HashSet<>(currErrors);
                newErrors.removeAll(prevErrors);

                Set<String> newCorrections = new HashSet<>(currCorrections);
                newCorrections.removeAll(prevCorrections);

                if (!newErrors.isEmpty() || !newCorrections.isEmpty()) {
                    history.add(new PatientHistoryDto(d.getDate(),
                            new ArrayList<>(newErrors),
                            new ArrayList<>(newCorrections)));
                }

                prevErrors = currErrors;
                prevCorrections = currCorrections;
            }

            return AsyncResult.success(history);
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
    private SpeechCard SpeechCardFromDto(BaseSpeechCard dto) {
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
        return speechCard;
    }
    private SpeechCard SpeechCardFromDto(SpeechCard oldCard) {
        SpeechCard newCard = new SpeechCard();
        newCard.setReason(oldCard.getReason());
        newCard.setStateOfHearning(oldCard.getStateOfHearning());
        newCard.setAnamnesis(oldCard.getAnamnesis());
        newCard.setGeneralMotor(oldCard.getGeneralMotor());
        newCard.setFineMotor(oldCard.getFineMotor());
        newCard.setArticulatory(oldCard.getArticulatory());
        newCard.setSoundReproduction(oldCard.getSoundReproduction());
        newCard.setSoundComponition(oldCard.getSoundComponition());
        newCard.setSpeechChars(oldCard.getSpeechChars());
        newCard.setPatientChars(oldCard.getPatientChars());
        newCard.setSpeechErrors(new HashSet<>(oldCard.getSpeechErrors()));
        newCard.setSoundCorrections(new HashSet<>(oldCard.getSoundCorrections()));
        return newCard;
    }

    private SpeechCardFullDto toFullDto(SpeechCard entity, Diagnostic diagnostic, Logoped logoped, Patient patient) {
        return new SpeechCardFullDto(entity.getId(),
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
                entity.getSpeechErrors().stream().map(SpeechError::getTitle).toList(),
                entity.getSoundCorrections().stream()
                        .map(sc -> sc.getSound() + ": " + sc.getCorrection())
                        .toList(),
                diagnostic.getDate(),
                logoped.getFirstName(),
                logoped.getLastName(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth());
    }


}
