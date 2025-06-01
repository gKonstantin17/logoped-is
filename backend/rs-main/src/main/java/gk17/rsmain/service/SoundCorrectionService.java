package gk17.rsmain.service;

import gk17.rsmain.dto.responseWrapper.AsyncResult;
import gk17.rsmain.dto.responseWrapper.ServiceResult;
import gk17.rsmain.dto.soundCorrection.SoundCorrectionDto;
import gk17.rsmain.dto.soundCorrection.SoundCorrectionReadDto;
import gk17.rsmain.entity.SoundCorrection;
import gk17.rsmain.repository.SoundCorrectionRepository;
import gk17.rsmain.utils.hibernate.ResponseHelper;
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
    public CompletableFuture<ServiceResult<List<SoundCorrectionReadDto>>> findall() {
        var data = repository.findAll();
        var result = data.stream().map(this::toReadDto).toList();
        return AsyncResult.success(result);
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
