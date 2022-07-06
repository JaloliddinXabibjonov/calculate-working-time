package uz.devops.service.mapper;

import org.mapstruct.*;
import uz.devops.domain.Worker;
import uz.devops.service.dto.WorkerDTO;

/**
 * Mapper for the entity {@link Worker} and its DTO {@link WorkerDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkerMapper extends EntityMapper<WorkerDTO, Worker> {}
