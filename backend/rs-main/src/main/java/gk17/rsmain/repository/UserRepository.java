package gk17.rsmain.repository;

import gk17.rsmain.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserData,Long> {
}
