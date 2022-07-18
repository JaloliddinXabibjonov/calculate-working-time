package uz.devops.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.devops.domain.WorkHistory;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkHistoryRepository;
import uz.devops.service.ButtonService;

@Service
public class ReasonDescriptionService {

    @Autowired
    private WorkHistoryRepository workHistoryRepository;

    @Autowired
    private ButtonService buttonService;

    public void save(boolean checkBoss, Long workerId, SendMessage sendMessage, String description) {
        Optional<WorkHistory> optionalWorkHistory = workHistoryRepository.findFirstByWorkerIdAndStartIsNotNullAndReasonDescriptionIsNullAndReasonIsNotNullOrderByStartDesc(
            workerId
        );
        if (optionalWorkHistory.isPresent()) {
            WorkHistory workHistory = optionalWorkHistory.get();
            workHistory.setReasonDescription(description);
            workHistoryRepository.save(workHistory);
            sendMessage.setText("Yuborilgan ma'lumotlar saqlandi.");
        } else {
            sendMessage.setText("Noto'g'ri buyruq berildi!");
        }
        sendMessage.setReplyMarkup(checkBoss ? buttonService.buttons(Status.START, Status.MANAGING) : buttonService.buttons(Status.START));
    }
}
