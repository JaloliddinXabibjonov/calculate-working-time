package uz.devops.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.service.ButtonService;
import uz.devops.service.WebhookService;

@Configuration
@ComponentScan(value = "uz.devops")
public class BotConfig {

    @Bean
    public WebhookService webhookService() {
        return new WebhookService();
    }

    @Bean
    public ButtonService buttonService() {
        return new ButtonService();
    }

    @Bean
    public ReplyKeyboardMarkup replyKeyboardMarkup() {
        return new ReplyKeyboardMarkup();
    }
}
