package uz.devops.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import uz.devops.domain.WorkHistory;
import uz.devops.service.dto.WorkingTimeDTO;

@Service
public class WorkingTimeCounterService {

    public WorkingTimeDTO counter(WorkHistory workHistory) {
        Instant start = workHistory.getStart();
        Instant toLunch = workHistory.getToLunch();
        Instant fromLunch = workHistory.getFromLunch();
        Instant end = workHistory.getEnd();
        long allMinutes = end.getEpochSecond() / 60 - start.getEpochSecond() / 60;
        long breakMinutes =
            (fromLunch == null ? 0 : fromLunch.getEpochSecond() / 60) - (toLunch == null ? 0 : toLunch.getEpochSecond() / 60);
        long allWorkingMinutes = allMinutes - breakMinutes;
        long workingHours = allWorkingMinutes / 60;
        long workingMinutes = allWorkingMinutes % 60;
        return new WorkingTimeDTO(workingHours, workingMinutes);
    }
}
