package uz.devops.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.domain.WorkHistory;

/**
 * Spring Data SQL repository for the WorkHistory entity.
 */
@Repository
public interface WorkHistoryRepository extends JpaRepository<WorkHistory, Long>, JpaSpecificationExecutor<WorkHistory> {
    boolean existsByWorkerIdAndStartIsNotNullAndStartBetween(Long worker_id, Instant start, Instant start2);
    boolean existsByWorkerIdAndStartIsNotNullAndEndIsNullAndStartBetween(Long worker_id, Instant start, Instant start2);

    Optional<WorkHistory> findFirstByWorkerIdAndStartIsNotNullAndStartBetweenOrderByStartDesc(
        Long worker_id,
        Instant start,
        Instant start2
    );
    Optional<WorkHistory> findTopByWorkerIdAndStartIsNotNullAndToLunchIsNotNullAndStartBetweenOrderByStartDesc(
        Long worker_id,
        Instant start,
        Instant start2
    );
    Optional<WorkHistory> findFirstByWorkerIdAndStartIsNotNullAndEndNullOrderByStartDesc(Long worker_id);
    Optional<WorkHistory> findFirstByWorkerIdAndStartIsNotNullAndReasonDescriptionIsNullAndReasonIsNotNullOrderByStartDesc(Long worker_id);

    Set<WorkHistory> getAllByStartBetweenOrEndBetween(Instant start, Instant start2, Instant end, Instant end2);

    Optional<WorkHistory> findFirstByEndNullAndStartBetweenOrderByStartDesc(Instant start, Instant start2);
}
