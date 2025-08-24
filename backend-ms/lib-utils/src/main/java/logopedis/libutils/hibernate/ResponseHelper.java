package logopedis.libutils.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public class ResponseHelper {
    public static <T> T findById(JpaRepository<T,Long> repository, Long id, String notFoundMessage) throws Exception {
        return repository.findById(id).orElseThrow(() -> new Exception(notFoundMessage));
    }
    public static <T> T findById(JpaRepository<T, UUID> repository, UUID id, String notFoundMessage) throws Exception {
        return repository.findById(id).orElseThrow(() -> new Exception(notFoundMessage));
    }
}
