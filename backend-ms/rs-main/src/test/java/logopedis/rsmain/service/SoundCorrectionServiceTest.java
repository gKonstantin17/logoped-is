package logopedis.rsmain.service;


import logopedis.libentities.rsmain.dto.responseWrapper.ServiceResult;
import logopedis.libentities.rsmain.dto.soundCorrection.SoundCorrectionDto;
import logopedis.libentities.rsmain.dto.soundCorrection.SoundCorrectionReadDto;
import logopedis.libentities.rsmain.entity.SoundCorrection;
import logopedis.rsmain.repository.SoundCorrectionRepository;
import logopedis.rsmain.utils.hibernate.ResponseHelper;
import logopedis.rsmain.service.SoundCorrectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoundCorrectionServiceTest {

    @Mock
    private SoundCorrectionRepository repository;

    @InjectMocks
    private SoundCorrectionService service;

    @Test
    void findall_ReturnsList() throws Exception {
        SoundCorrection sc = new SoundCorrection();
        sc.setId(1L);
        sc.setSound("S");
        sc.setCorrection("C");

        when(repository.findAll()).thenReturn(List.of(sc));

        CompletableFuture<ServiceResult<List<SoundCorrectionReadDto>>> future = service.findall();
        ServiceResult<List<SoundCorrectionReadDto>> result = future.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).id()).isEqualTo(1L);
        assertThat(result.data().get(0).sound()).isEqualTo("S");
        assertThat(result.data().get(0).correction()).isEqualTo("C");

        verify(repository).findAll();
    }

    @Test
    void create_ReturnsSavedCorrection() throws Exception {
        SoundCorrectionDto dto = new SoundCorrectionDto("S", "C");
        SoundCorrection saved = new SoundCorrection();
        saved.setId(1L);
        saved.setSound(dto.sound());
        saved.setCorrection(dto.correction());

        when(repository.save(any(SoundCorrection.class))).thenReturn(saved);

        CompletableFuture<ServiceResult<SoundCorrectionReadDto>> future = service.create(dto);
        ServiceResult<SoundCorrectionReadDto> result = future.get();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.data().id()).isEqualTo(1L);
        assertThat(result.data().sound()).isEqualTo("S");
        assertThat(result.data().correction()).isEqualTo("C");

        verify(repository).save(any(SoundCorrection.class));
    }

    @Test
    void update_ReturnsUpdatedCorrection() throws Exception {
        Long id = 1L;
        SoundCorrection existing = new SoundCorrection();
        existing.setId(id);
        existing.setSound("Old");
        existing.setCorrection("OldC");

        SoundCorrectionDto dto = new SoundCorrectionDto("New", "NewC");

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository, id, "Направление коррекции не найдено"))
                    .thenReturn(existing);

            when(repository.save(any(SoundCorrection.class))).thenAnswer(inv -> inv.getArgument(0));

            CompletableFuture<ServiceResult<SoundCorrectionReadDto>> future = service.update(id, dto);
            ServiceResult<SoundCorrectionReadDto> result = future.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data().id()).isEqualTo(id);
            assertThat(result.data().sound()).isEqualTo("New");
            assertThat(result.data().correction()).isEqualTo("NewC");

            verify(repository).save(existing);
        }
    }

    @Test
    void delete_ReturnsId() throws Exception {
        Long id = 1L;
        SoundCorrection existing = new SoundCorrection();
        existing.setId(id);

        try (var mocked = mockStatic(ResponseHelper.class)) {
            mocked.when(() -> ResponseHelper.findById(repository, id, "Направление коррекции не найдено"))
                    .thenReturn(existing);

            CompletableFuture<ServiceResult<Long>> future = service.delete(id);
            ServiceResult<Long> result = future.get();

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.data()).isEqualTo(id);

            verify(repository).deleteById(id);
        }
    }
}
