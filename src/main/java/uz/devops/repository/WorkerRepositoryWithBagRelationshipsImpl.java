package uz.devops.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.devops.domain.Worker;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class WorkerRepositoryWithBagRelationshipsImpl implements WorkerRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Worker> fetchBagRelationships(Optional<Worker> worker) {
        return worker.map(this::fetchRoles);
    }

    @Override
    public Page<Worker> fetchBagRelationships(Page<Worker> workers) {
        return new PageImpl<>(fetchBagRelationships(workers.getContent()), workers.getPageable(), workers.getTotalElements());
    }

    @Override
    public List<Worker> fetchBagRelationships(List<Worker> workers) {
        return Optional.of(workers).map(this::fetchRoles).orElse(Collections.emptyList());
    }

    Worker fetchRoles(Worker result) {
        return entityManager
            .createQuery("select worker from Worker worker left join fetch worker.roles where worker is :worker", Worker.class)
            .setParameter("worker", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Worker> fetchRoles(List<Worker> workers) {
        return entityManager
            .createQuery("select distinct worker from Worker worker left join fetch worker.roles where worker in :workers", Worker.class)
            .setParameter("workers", workers)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
