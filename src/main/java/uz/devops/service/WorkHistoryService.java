package uz.devops.service;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.devops.domain.WorkHistory;
import uz.devops.service.dto.WorkHistoryDTO;

/**
 * Service Interface for managing {@link uz.devops.domain.WorkHistory}.
 */
public interface WorkHistoryService {
    /**
     * Save a workHistory.
     *
     * @param workHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    WorkHistoryDTO save(WorkHistoryDTO workHistoryDTO);

    /**
     * Updates a workHistory.
     *
     * @param workHistoryDTO the entity to update.
     * @return the persisted entity.
     */
    WorkHistoryDTO update(WorkHistoryDTO workHistoryDTO);

    /**
     * Partially updates a workHistory.
     *
     * @param workHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WorkHistoryDTO> partialUpdate(WorkHistoryDTO workHistoryDTO);

    /**
     * Get all the workHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<WorkHistoryDTO> findAll(Pageable pageable);

    /**
     * Get the "id" workHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WorkHistoryDTO> findOne(Long id);

    /**
     * Delete the "id" workHistory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Set<WorkHistory> getAllToday();
}
