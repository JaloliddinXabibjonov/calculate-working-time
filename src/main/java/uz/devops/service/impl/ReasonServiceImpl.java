package uz.devops.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.domain.Reason;
import uz.devops.repository.ReasonRepository;
import uz.devops.service.ReasonService;
import uz.devops.service.dto.ReasonDTO;
import uz.devops.service.mapper.ReasonMapper;

/**
 * Service Implementation for managing {@link Reason}.
 */
@Service
@Transactional
public class ReasonServiceImpl implements ReasonService {

    private final Logger log = LoggerFactory.getLogger(ReasonServiceImpl.class);

    private final ReasonRepository reasonRepository;

    private final ReasonMapper reasonMapper;

    public ReasonServiceImpl(ReasonRepository reasonRepository, ReasonMapper reasonMapper) {
        this.reasonRepository = reasonRepository;
        this.reasonMapper = reasonMapper;
    }

    @Override
    public ReasonDTO save(ReasonDTO reasonDTO) {
        log.debug("Request to save Reason : {}", reasonDTO);
        Reason reason = reasonMapper.toEntity(reasonDTO);
        reason = reasonRepository.save(reason);
        return reasonMapper.toDto(reason);
    }

    @Override
    public ReasonDTO update(ReasonDTO reasonDTO) {
        log.debug("Request to save Reason : {}", reasonDTO);
        Reason reason = reasonMapper.toEntity(reasonDTO);
        reason = reasonRepository.save(reason);
        return reasonMapper.toDto(reason);
    }

    @Override
    public Optional<ReasonDTO> partialUpdate(ReasonDTO reasonDTO) {
        log.debug("Request to partially update Reason : {}", reasonDTO);

        return reasonRepository
            .findById(reasonDTO.getId())
            .map(existingReason -> {
                reasonMapper.partialUpdate(existingReason, reasonDTO);

                return existingReason;
            })
            .map(reasonRepository::save)
            .map(reasonMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReasonDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Reasons");
        return reasonRepository.findAll(pageable).map(reasonMapper::toDto);
    }

    /**
     *  Get all the reasons where Action is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ReasonDTO> findAllWhereActionIsNull() {
        log.debug("Request to get all reasons where Action is null");
        return StreamSupport
            .stream(reasonRepository.findAll().spliterator(), false)
            .filter(reason -> reason.getAction() == null)
            .map(reasonMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReasonDTO> findOne(Long id) {
        log.debug("Request to get Reason : {}", id);
        return reasonRepository.findById(id).map(reasonMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Reason : {}", id);
        reasonRepository.deleteById(id);
    }
}
