package logopedis.rsmain.repository;

import logopedis.rsmain.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserData, UUID> {
}
