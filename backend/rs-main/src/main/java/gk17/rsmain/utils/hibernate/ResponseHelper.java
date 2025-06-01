package gk17.rsmain.utils.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;

public class ResponseHelper {
    public static <T> T findById(JpaRepository<T,Long> repository, Long id, String notFoundMessage) throws Exception {
        return repository.findById(id).orElseThrow(() -> new Exception(notFoundMessage));
    }
}
