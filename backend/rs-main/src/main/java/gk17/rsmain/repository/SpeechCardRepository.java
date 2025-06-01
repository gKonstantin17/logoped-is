package gk17.rsmain.repository;

import gk17.rsmain.entity.SpeechCard;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface SpeechCardRepository extends JpaRepository<SpeechCard,Long> {
    @NonNull
    @EntityGraph(attributePaths = {"speechErrors", "soundCorrections"})
    List<SpeechCard> findAll();

    @NonNull
    @EntityGraph(attributePaths = {"speechErrors", "soundCorrections"})
    Optional<SpeechCard> findById(@NonNull Long id);
}
