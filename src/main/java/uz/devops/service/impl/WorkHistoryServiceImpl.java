package uz.devops.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.domain.WorkHistory;
import uz.devops.repository.WorkHistoryRepository;
import uz.devops.service.WorkHistoryService;
import uz.devops.service.dto.WorkHistoryDTO;
import uz.devops.service.mapper.WorkHistoryMapper;

/**
 * Service Implementation for managing {@link WorkHistory}.
 */
@Service
@Transactional
public class WorkHistoryServiceImpl implements WorkHistoryService {

    private final Logger log = LoggerFactory.getLogger(WorkHistoryServiceImpl.class);

    private final WorkHistoryRepository workHistoryRepository;

    private final WorkHistoryMapper workHistoryMapper;

    private final ZoneId zoneId = ZoneId.of("Asia/Tashkent");

    public WorkHistoryServiceImpl(WorkHistoryRepository workHistoryRepository, WorkHistoryMapper workHistoryMapper) {
        this.workHistoryRepository = workHistoryRepository;
        this.workHistoryMapper = workHistoryMapper;
    }

    @Override
    public WorkHistoryDTO save(WorkHistoryDTO workHistoryDTO) {
        log.debug("Request to save WorkHistory : {}", workHistoryDTO);
        WorkHistory workHistory = workHistoryMapper.toEntity(workHistoryDTO);
        workHistory = workHistoryRepository.save(workHistory);
        return workHistoryMapper.toDto(workHistory);
    }

    @Override
    public WorkHistoryDTO update(WorkHistoryDTO workHistoryDTO) {
        log.debug("Request to save WorkHistory : {}", workHistoryDTO);
        WorkHistory workHistory = workHistoryMapper.toEntity(workHistoryDTO);
        workHistory = workHistoryRepository.save(workHistory);
        return workHistoryMapper.toDto(workHistory);
    }

    @Override
    public Optional<WorkHistoryDTO> partialUpdate(WorkHistoryDTO workHistoryDTO) {
        log.debug("Request to partially update WorkHistory : {}", workHistoryDTO);

        return workHistoryRepository
            .findById(workHistoryDTO.getId())
            .map(existingWorkHistory -> {
                workHistoryMapper.partialUpdate(existingWorkHistory, workHistoryDTO);

                return existingWorkHistory;
            })
            .map(workHistoryRepository::save)
            .map(workHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkHistoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all WorkHistories");
        return workHistoryRepository.findAll(pageable).map(workHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkHistoryDTO> findOne(Long id) {
        log.debug("Request to get WorkHistory : {}", id);
        return workHistoryRepository.findById(id).map(workHistoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete WorkHistory : {}", id);
        workHistoryRepository.deleteById(id);
    }

    @Override
    public Set<WorkHistory> getAllToday() {
        return workHistoryRepository.getAllByStartBetweenOrEndBetween(
            LocalDate.now().atStartOfDay(zoneId).toInstant(),
            LocalDate.now().plusDays(1).atStartOfDay(zoneId).toInstant(),
            LocalDate.now().atStartOfDay(zoneId).toInstant(),
            LocalDate.now().plusDays(1).atStartOfDay(zoneId).toInstant()
        );
    }
}
