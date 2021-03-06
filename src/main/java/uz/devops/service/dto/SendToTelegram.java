package uz.devops.service.dto;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.devops.config.Constants;

@Service
public class SendToTelegram {

    private final RestTemplate restTemplate = new RestTemplate();

    public void send(SendMessage sendMessage) {
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
    }
}
