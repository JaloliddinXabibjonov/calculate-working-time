package uz.devops.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.devops.service.dto.ReasonDTO;

/**
 * Service Interface for managing {@link uz.devops.domain.Reason}.
 */
public interface ReasonService {
    /**
     * Save a reason.
     *
     * @param reasonDTO the entity to save.
     * @return the persisted entity.
     */
    ReasonDTO save(ReasonDTO reasonDTO);

    /**
     * Updates a reason.
     *
     * @param reasonDTO the entity to update.
     * @return the persisted entity.
     */
    ReasonDTO update(ReasonDTO reasonDTO);

    /**
     * Partially updates a reason.
     *
     * @param reasonDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ReasonDTO> partialUpdate(ReasonDTO reasonDTO);

    /**
     * Get all the reasons.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReasonDTO> findAll(Pageable pageable);
    /**
     * Get all the ReasonDTO where Action is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<ReasonDTO> findAllWhereActionIsNull();

    /**
     * Get the "id" reason.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ReasonDTO> findOne(Long id);

    /**
     * Delete the "id" reason.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
