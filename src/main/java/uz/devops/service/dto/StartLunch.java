package uz.devops.service.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
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

@Service
public class StartLunch extends AbstractBot {

    @Autowired
    ButtonService buttonService;

    private final WorkHistoryRepository workHistoryRepository;

    public StartLunch(WorkHistoryRepository workHistoryRepository) {
        this.workHistoryRepository = workHistoryRepository;
    }

    private final ZoneId zoneId = ZoneId.of("Asia/Tashkent");

    @Override
    public void start(Update update, String message, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        super.start(update, message, worker, checkBoss, sendMessage);
        botService(update, worker, checkBoss, sendMessage);
    }

    @Override
    public void botService(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        Optional<WorkHistory> optionalWorkHistory = workHistoryRepository.findFirstByWorkerIdAndStartIsNotNullAndStartBetweenOrderByStartDesc(
            worker.getId(),
            LocalDate.now().atStartOfDay(zoneId).toInstant(),
            LocalDate.now().atStartOfDay(zoneId).plusSeconds(86400).toInstant()
        );
        if (optionalWorkHistory.isPresent()) {
            WorkHistory workHistory = optionalWorkHistory.get();
            workHistory.setStatus(Status.ACTIVE);
            workHistory.setWorker(worker);
            workHistory.setToLunch(Instant.now());
            workHistoryRepository.save(workHistory);
            sendMessage.setText("Yoqimli ishtaha");
        } else sendMessage.setText("Siz hali ishga  kelmagansiz!");
        ReplyKeyboardMarkup replyKeyboardMarkup1 = checkBoss
            ? buttonService.buttons(Status.AFTER_LUNCH, Status.MANAGING)
            : buttonService.buttons(Status.AFTER_LUNCH, Status.AFTER_LUNCH);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
    }
}
