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
public class EndLunch extends AbstractBot {

    @Autowired
    ButtonService buttonService;

    private final WorkHistoryRepository workHistoryRepository;

    public EndLunch(WorkHistoryRepository workHistoryRepository) {
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
        Optional<WorkHistory> optionalWorkHistory = workHistoryRepository.findTopByWorkerIdAndStartIsNotNullAndToLunchIsNotNullAndStartBetweenOrderByStartDesc(
            worker.getId(),
            LocalDate.now().atStartOfDay(zoneId).toInstant(),
            LocalDate.now().atStartOfDay(zoneId).plusSeconds(86400).toInstant()
        );
        if (optionalWorkHistory.isPresent()) {
            WorkHistory workHistory = optionalWorkHistory.get();
            workHistory.setStatus(Status.ACTIVE);
            workHistory.setWorker(worker);
            workHistory.setFromLunch(Instant.now());
            workHistoryRepository.save(workHistory);
        } else sendMessage.setText("Siz hali tushlikka chiqmagansiz!");
        ReplyKeyboardMarkup replyKeyboardMarkup1 = checkBoss
            ? buttonService.buttons(Status.GO_HOME, Status.MANAGING)
            : buttonService.buttons(Status.GO_HOME, Status.GO_HOME);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
    }
}
