package uz.devops.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
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

@Service(DataLoader.START_LUNCH)
public class StartLunchCommandService extends AbstractBot {

    @Autowired
    private ButtonService buttonService;

    @Autowired
    private WorkHistoryRepository workHistoryRepository;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        Optional<WorkHistory> optionalWorkHistory = workHistoryRepository.findFirstByWorkerIdAndStartIsNotNullAndStartBetweenOrderByStartDesc(
            worker.getId(),
            LocalDate.now().atStartOfDay(DataLoader.ZONE_ID).toInstant(),
            LocalDate.now().atStartOfDay(DataLoader.ZONE_ID).plusSeconds(86400).toInstant()
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
            ? buttonService.buttons(Status.END_LUNCH, Status.MANAGING)
            : buttonService.buttons(Status.END_LUNCH);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
    }
}
