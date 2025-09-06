package logopedis.rsmain.repository;

import logopedis.libentities.rsmain.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserData, UUID> {
    @Query("""
           SELECT p.user FROM Patient p
           WHERE p.id = :id
           """)
    UserData findByPatient(@Param("id") Long id);
}
