package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Worker;

@Service
public class SaveVacationDayService extends AbstractBot {

    @Autowired
    private ReasonDescriptionService reasonDescriptionService;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        reasonDescriptionService.save(checkBoss, worker.getId(), sendMessage, update.getMessage().getText());
    }
}
