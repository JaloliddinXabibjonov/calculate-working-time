package uz.devops.service;

import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.config.Constants;
import uz.devops.domain.Worker;
import uz.devops.service.dto.ResultTelegram;
import uz.devops.service.dto.SendToTelegram;

public abstract class AbstractBot {

    @Autowired
    private SendToTelegram sendToTelegram;

    public void reply(Update update, Worker worker, boolean checkBoss) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.enableHtml(true);

        process(update, worker, checkBoss, sendMessage);
        sendToTelegram.send(sendMessage);
    }

    public void reply(Update update, Worker worker, boolean checkBoss, boolean checkInlineButton) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(
            checkInlineButton ? update.getCallbackQuery().getMessage().getChatId().toString() : update.getMessage().getChatId().toString()
        );
        sendMessage.enableHtml(true);

        process(update, worker, checkBoss, sendMessage);
        sendToTelegram.send(sendMessage);
    }

    public void reply(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(message);
        sendMessage.enableHtml(true);

        sendToTelegram.send(sendMessage);
    }

    protected abstract void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage);
}
