package logopedis.rsmain.repository;

import logopedis.rsmain.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkRepository extends JpaRepository<Homework,Long> {
}
