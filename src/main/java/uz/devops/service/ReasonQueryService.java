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
import uz.devops.domain.Reason;
import uz.devops.repository.ReasonRepository;
import uz.devops.service.criteria.ReasonCriteria;
import uz.devops.service.dto.ReasonDTO;
import uz.devops.service.mapper.ReasonMapper;

/**
 * Service for executing complex queries for {@link Reason} entities in the database.
 * The main input is a {@link ReasonCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ReasonDTO} or a {@link Page} of {@link ReasonDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReasonQueryService extends QueryService<Reason> {

    private final Logger log = LoggerFactory.getLogger(ReasonQueryService.class);

    private final ReasonRepository reasonRepository;

    private final ReasonMapper reasonMapper;

    public ReasonQueryService(ReasonRepository reasonRepository, ReasonMapper reasonMapper) {
        this.reasonRepository = reasonRepository;
        this.reasonMapper = reasonMapper;
    }

    /**
     * Return a {@link List} of {@link ReasonDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ReasonDTO> findByCriteria(ReasonCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Reason> specification = createSpecification(criteria);
        return reasonMapper.toDto(reasonRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ReasonDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReasonDTO> findByCriteria(ReasonCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Reason> specification = createSpecification(criteria);
        return reasonRepository.findAll(specification, page).map(reasonMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReasonCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Reason> specification = createSpecification(criteria);
        return reasonRepository.count(specification);
    }

    /**
     * Function to convert {@link ReasonCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Reason> createSpecification(ReasonCriteria criteria) {
        Specification<Reason> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Reason_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Reason_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Reason_.description));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Reason_.status));
            }
            if (criteria.getParentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getParentId(), root -> root.join(Reason_.parent, JoinType.LEFT).get(Reason_.id))
                    );
            }
            if (criteria.getActionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getActionId(), root -> root.join(Reason_.action, JoinType.LEFT).get(Action_.id))
                    );
            }
            if (criteria.getWorkHistoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getWorkHistoryId(),
                            root -> root.join(Reason_.workHistories, JoinType.LEFT).get(WorkHistory_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
