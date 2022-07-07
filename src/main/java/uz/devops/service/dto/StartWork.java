package uz.devops.service.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.domain.WorkHistory;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkHistoryRepository;
import uz.devops.service.ButtonService;
import uz.devops.service.WebhookService;

@Service
public class StartWork extends AbstractBot {

    private final WorkHistoryRepository workHistoryRepository;

    @Autowired
    ButtonService buttonService;

    public StartWork(WorkHistoryRepository workHistoryRepository) {
        this.workHistoryRepository = workHistoryRepository;
    }

    private final ZoneId zoneId = ZoneId.of("Asia/Tashkent");

    @Override
    public void reply(Update update, String message, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        botService(update, worker, checkBoss, sendMessage);
        super.reply(update, message, worker, checkBoss, sendMessage);
    }

    @Override
    public void botService(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        boolean exists = workHistoryRepository.existsByWorkerIdAndStartIsNotNullAndEndIsNullAndReasonIsNotNullAndStartBetween(
            worker.getId(),
            LocalDate.now().atStartOfDay(zoneId).toInstant(),
            LocalDate.now().atStartOfDay(zoneId).plusSeconds(86400).toInstant()
        );
        if (!exists) {
            workHistoryRepository.save(new WorkHistory(Instant.now(), Status.ACTIVE, worker));
        } else sendMessage.setText("Siz allaqachon ishga kelgansiz!");
        ReplyKeyboardMarkup replyKeyboardMarkup1 = checkBoss
            ? buttonService.buttons(Status.AT_WORK, Status.GO_HOME, Status.MANAGING)
            : buttonService.buttons(Status.AT_WORK, Status.GO_HOME);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
    }
}
