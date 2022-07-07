package uz.devops.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import uz.devops.domain.Worker;
import uz.devops.service.dto.WorkerDTO;

/**
 * Mapper for the entity {@link Worker} and its DTO {@link WorkerDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkerMapper extends EntityMapper<WorkerDTO, Worker> {
    @Mapping(target = "role", source = "role")
    WorkerDTO toDto(Worker s);

    @Mapping(target = "role", ignore = true)
    Worker toEntity(WorkerDTO workerDTO);
}
