package gk17.rsmain.repository;

import gk17.rsmain.entity.Lesson;
import gk17.rsmain.entity.SpeechCard;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpeechCardRepository extends JpaRepository<SpeechCard,Long> {
    @EntityGraph(attributePaths = {"speechErrors", "soundCorrections"})
    List<SpeechCard> findAll();
}
