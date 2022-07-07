package uz.devops.service.dto;

import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.config.Constants;
import uz.devops.domain.Worker;

public abstract class AbstractBot {

    private final RestTemplate restTemplate = new RestTemplate();

    public void reply(Update update, String message, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.enableHtml(true);
        sendMessage.setText(message);
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
    }

    public void reply(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.enableHtml(true);
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
    }

    public abstract void botService(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage);
}
