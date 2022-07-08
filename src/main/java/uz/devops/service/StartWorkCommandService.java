package uz.devops.service;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.component.DataLoader;
import uz.devops.domain.WorkHistory;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkHistoryRepository;

@Service(DataLoader.START_WORK)
public class StartWorkCommandService extends AbstractBot {

    @Autowired
    private WorkHistoryRepository workHistoryRepository;

    @Autowired
    private ButtonService buttonService;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        boolean exists = workHistoryRepository.existsByWorkerIdAndStartIsNotNullAndEndIsNullAndReasonIsNotNullAndStartBetween(
            worker.getId(),
            LocalDate.now().atStartOfDay(DataLoader.ZONE_ID).toInstant(),
            LocalDate.now().atStartOfDay(DataLoader.ZONE_ID).plusSeconds(86400).toInstant()
        );
        if (!exists) {
            workHistoryRepository.save(new WorkHistory(Instant.now(), Status.ACTIVE, worker));
            sendMessage.setText("Kun davomida yaxshi kayfiyat tilaymiz!");
        } else {
            sendMessage.setText("Siz allaqachon ishga kelgansiz!");
        }
        ReplyKeyboardMarkup replyKeyboardMarkup1 = checkBoss
            ? buttonService.buttons(Status.AT_WORK, Status.GO_HOME, Status.MANAGING)
            : buttonService.buttons(Status.AT_WORK, Status.GO_HOME);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
    }
}
