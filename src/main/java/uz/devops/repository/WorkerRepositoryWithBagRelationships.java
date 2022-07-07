package uz.devops.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import uz.devops.domain.Worker;

public interface WorkerRepositoryWithBagRelationships {
    Optional<Worker> fetchBagRelationships(Optional<Worker> worker);

    List<Worker> fetchBagRelationships(List<Worker> workers);

    Page<Worker> fetchBagRelationships(Page<Worker> workers);
}
