package uz.devops.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.domain.Worker;

/**
 * Spring Data SQL repository for the Worker entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long>, JpaSpecificationExecutor<Worker> {
    List<Worker> findAllByWorkerTgId(@NotNull Long workerTgId);

    Set<Worker> findAllByRole(String boss);
}
