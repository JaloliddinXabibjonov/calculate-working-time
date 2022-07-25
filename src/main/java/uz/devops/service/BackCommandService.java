package uz.devops.service;

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

import java.time.LocalDate;
import java.util.Optional;

@Service(DataLoader.BACK)
public class BackCommandService extends AbstractBot {

    @Autowired
    private ButtonService buttonService;

    @Autowired
    private WorkHistoryRepository workHistoryRepository;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        Optional<WorkHistory> optionalWorkHistory = workHistoryRepository.findFirstByEndNullAndStartBetweenOrderByStartDesc(LocalDate.now().atStartOfDay(DataLoader.ZONE_ID).toInstant(), LocalDate.now().atStartOfDay(DataLoader.ZONE_ID).plusDays(1).toInstant());
        sendMessage.setText("Kerakli bo‘limni tanlang! \uD83D\uDC47 ");
        ReplyKeyboardMarkup replyKeyboardMarkup = null;
        if (optionalWorkHistory.isPresent()) {
            WorkHistory workHistory = optionalWorkHistory.get();
            switch (workHistory.getStatus()) {
                case AT_WORK:
                    replyKeyboardMarkup = checkBoss
                        ? buttonService.buttons(Status.AT_WORK, Status.GO_HOME, Status.MANAGING)
                        : buttonService.buttons(Status.AT_WORK, Status.GO_HOME);
                    break;
                case START_LUNCH:
                    replyKeyboardMarkup = checkBoss
                        ? buttonService.buttons(Status.END_LUNCH, Status.MANAGING)
                        : buttonService.buttons(Status.END_LUNCH);
                    break;
                case END_LUNCH:
                    replyKeyboardMarkup = checkBoss
                        ? buttonService.buttons(Status.GO_HOME, Status.MANAGING)
                        : buttonService.buttons(Status.GO_HOME);
                    break;
            }
        }else {
            replyKeyboardMarkup= checkBoss
                ? buttonService.buttons(Status.START, Status.MANAGING)
                : buttonService.buttons(Status.START);
        }
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }


}
