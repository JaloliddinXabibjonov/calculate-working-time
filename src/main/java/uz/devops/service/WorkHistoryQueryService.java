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
import uz.devops.domain.WorkHistory;
import uz.devops.repository.WorkHistoryRepository;
import uz.devops.service.criteria.WorkHistoryCriteria;
import uz.devops.service.dto.WorkHistoryDTO;
import uz.devops.service.mapper.WorkHistoryMapper;

/**
 * Service for executing complex queries for {@link WorkHistory} entities in the database.
 * The main input is a {@link WorkHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WorkHistoryDTO} or a {@link Page} of {@link WorkHistoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WorkHistoryQueryService extends QueryService<WorkHistory> {

    private final Logger log = LoggerFactory.getLogger(WorkHistoryQueryService.class);

    private final WorkHistoryRepository workHistoryRepository;

    private final WorkHistoryMapper workHistoryMapper;

    public WorkHistoryQueryService(WorkHistoryRepository workHistoryRepository, WorkHistoryMapper workHistoryMapper) {
        this.workHistoryRepository = workHistoryRepository;
        this.workHistoryMapper = workHistoryMapper;
    }

    /**
     * Return a {@link List} of {@link WorkHistoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WorkHistoryDTO> findByCriteria(WorkHistoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<WorkHistory> specification = createSpecification(criteria);
        return workHistoryMapper.toDto(workHistoryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link WorkHistoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkHistoryDTO> findByCriteria(WorkHistoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WorkHistory> specification = createSpecification(criteria);
        return workHistoryRepository.findAll(specification, page).map(workHistoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WorkHistoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<WorkHistory> specification = createSpecification(criteria);
        return workHistoryRepository.count(specification);
    }

    /**
     * Function to convert {@link WorkHistoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WorkHistory> createSpecification(WorkHistoryCriteria criteria) {
        Specification<WorkHistory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), WorkHistory_.id));
            }
            if (criteria.getStart() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStart(), WorkHistory_.start));
            }
            if (criteria.getToLunch() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getToLunch(), WorkHistory_.toLunch));
            }
            if (criteria.getFromLunch() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFromLunch(), WorkHistory_.fromLunch));
            }
            if (criteria.getEnd() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEnd(), WorkHistory_.end));
            }
            if (criteria.getReasonDescription() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getReasonDescription(), WorkHistory_.reasonDescription));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), WorkHistory_.status));
            }
            if (criteria.getWorkerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getWorkerId(), root -> root.join(WorkHistory_.worker, JoinType.LEFT).get(Worker_.id))
                    );
            }
            if (criteria.getReasonId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getReasonId(), root -> root.join(WorkHistory_.reason, JoinType.LEFT).get(Reason_.id))
                    );
            }
        }
        return specification;
    }
}
