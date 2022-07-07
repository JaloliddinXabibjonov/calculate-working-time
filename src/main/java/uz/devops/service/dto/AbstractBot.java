package uz.devops.service.dto;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Worker;

public abstract class AbstractBot {

    public void start(Update update, String message, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.enableHtml(true);
        sendMessage.setText(message);
    }

    public abstract void botService(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage);
}
