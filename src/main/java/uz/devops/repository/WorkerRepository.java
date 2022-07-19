package uz.devops.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;

/**
 * Spring Data SQL repository for the Worker entity.
 */
@Repository
public interface WorkerRepository
    extends WorkerRepositoryWithBagRelationships, JpaRepository<Worker, Long>, JpaSpecificationExecutor<Worker> {
    default Optional<Worker> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Worker> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Worker> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    Set<Worker> findAllByRoleAndStatus(String role, Status status);
    Set<Worker> findAllByStatus(Status status);

    List<Worker> findAllByWorkerTgIdAndStatus(Long id, Status active);

    Worker getByWorkerTgIdAndRole(Long removeWorkerTgId, String role);
}
