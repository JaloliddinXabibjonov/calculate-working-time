package uz.devops.service;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Reason;
import uz.devops.domain.WorkHistory;
import uz.devops.domain.Worker;
import uz.devops.repository.ReasonRepository;
import uz.devops.repository.WorkHistoryRepository;

@Service
public class SaveReasonService {

    @Autowired
    private ReasonRepository reasonRepository;

    @Autowired
    private WorkHistoryRepository workHistoryRepository;

    public void saveReason(Update update, Worker worker) {
        WorkHistory workHistory = new WorkHistory();
        Reason reason = reasonRepository.getByName(update.getMessage().getText());
        workHistory.setReason(reason);
        workHistory.setWorker(worker);
        workHistory.setStart(Instant.now());
        workHistoryRepository.save(workHistory);
    }
}
