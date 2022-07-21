package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;

@Service
public class InvalidCommandService extends AbstractBot {

    @Autowired
    ButtonService buttonService;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        sendMessage.setText("Noto'g'ri buyruq berildi!");
        sendMessage.setReplyMarkup(checkBoss ? buttonService.buttons(Status.START, Status.MANAGING) : buttonService.buttons(Status.START));
    }
}
