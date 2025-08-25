package logopedis.msnotification.repository;

import logopedis.libentities.msnotification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}