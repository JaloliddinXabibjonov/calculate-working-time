package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.component.DataLoader;
import uz.devops.domain.Worker;

@Service(DataLoader.VACATION)
public class VacationCommandService extends AbstractBot {

    @Autowired
    private ButtonService buttonService;

    @Autowired
    private SaveReasonService saveReasonService;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        sendMessage.setText("Necha kun dam olmoqchisiz?");
        saveReasonService.saveReason(update, worker);
        sendMessage.setReplyMarkup(buttonService.descriptionOfReasonButtons());
    }
}
