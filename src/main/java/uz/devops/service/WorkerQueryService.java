package uz.devops.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import uz.devops.domain.*; // for static metamodels
import uz.devops.domain.Worker;
import uz.devops.repository.WorkerRepository;
import uz.devops.service.criteria.WorkerCriteria;
import uz.devops.service.dto.WorkerDTO;
import uz.devops.service.mapper.WorkerMapper;

/**
 * Service for executing complex queries for {@link Worker} entities in the database.
 * The main input is a {@link WorkerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WorkerDTO} or a {@link Page} of {@link WorkerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WorkerQueryService extends QueryService<Worker> {

    private final Logger log = LoggerFactory.getLogger(WorkerQueryService.class);

    private final WorkerRepository workerRepository;

    private final WorkerMapper workerMapper;

    public WorkerQueryService(WorkerRepository workerRepository, WorkerMapper workerMapper) {
        this.workerRepository = workerRepository;
        this.workerMapper = workerMapper;
    }

    /**
     * Return a {@link List} of {@link WorkerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WorkerDTO> findByCriteria(WorkerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Worker> specification = createSpecification(criteria);
        return workerMapper.toDto(workerRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link WorkerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkerDTO> findByCriteria(WorkerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Worker> specification = createSpecification(criteria);
        return workerRepository.findAll(specification, page).map(workerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WorkerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Worker> specification = createSpecification(criteria);
        return workerRepository.count(specification);
    }

    /**
     * Function to convert {@link WorkerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Worker> createSpecification(WorkerCriteria criteria) {
        Specification<Worker> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Worker_.id));
            }
            if (criteria.getFullName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullName(), Worker_.fullName));
            }
            if (criteria.getWorkerTgId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getWorkerTgId(), Worker_.workerTgId));
            }
            if (criteria.getRole() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRole(), Worker_.role));
            }
            if (criteria.getWorkHistoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getWorkHistoryId(),
                            root -> root.join(Worker_.workHistories, JoinType.LEFT).get(WorkHistory_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
