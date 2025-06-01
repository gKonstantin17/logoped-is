package gk17.rsmain.service;

import gk17.rsmain.dto.diagnostic.DiagnosticDto;
import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.dto.soundCorrection.SoundCorrectionDto;
import gk17.rsmain.entity.Diagnostic;
import gk17.rsmain.entity.SoundCorrection;
import gk17.rsmain.entity.UserData;
import gk17.rsmain.repository.DiagnosticRepository;
import gk17.rsmain.repository.SoundCorrectionRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class SoundCorrectionService {
    private final SoundCorrectionRepository repository;

    public SoundCorrectionService(SoundCorrectionRepository repository) {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<ServiceResult<List<SoundCorrection>>> findall() {
        var data = repository.findAll();
        return AsyncResult.success(data);
    }

    @Async
    public CompletableFuture<ServiceResult<SoundCorrection>> create(SoundCorrectionDto dto) {
        try {
            SoundCorrection soundCorrection = new SoundCorrection();
            soundCorrection.setSound(dto.sound());
            soundCorrection.setCorrection(dto.correction());

            SoundCorrection result = repository.save(soundCorrection);
            return AsyncResult.success(result);
        } catch( Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }

}
    @Async
    public CompletableFuture<ServiceResult<SoundCorrection>> update(Long id, SoundCorrectionDto dto) {
        try {
            var data = repository.findById(id);
            if (data.isEmpty())
                return AsyncResult.error("Направление коррекции не найдено");
            var result = data.get();

            if (dto.sound() != null) result.setSound(dto.sound());
            if (dto.correction() != null) result.setCorrection(dto.correction());

            return AsyncResult.success(repository.save(result));
        } catch (Exception ex) {
            return AsyncResult.error(ex.getMessage());
        }
    }
    @Async
    public CompletableFuture<ServiceResult<SoundCorrection>> delete(Long id) {
        var result = repository.findById(id);
        if (result.isEmpty())
            return AsyncResult.error("Направление коррекции не найден");

        var deletedData = result.get();
        repository.deleteById(id);
        return AsyncResult.success(deletedData);
    }
}
