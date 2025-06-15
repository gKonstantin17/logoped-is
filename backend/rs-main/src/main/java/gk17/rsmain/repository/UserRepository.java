package gk17.rsmain.repository;

import gk17.rsmain.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserData, UUID> {
}
