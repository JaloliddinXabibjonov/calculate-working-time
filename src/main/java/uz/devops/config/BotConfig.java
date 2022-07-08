package uz.devops.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.service.ButtonService;

@Configuration
@ComponentScan(value = "uz.devops")
public class BotConfig {

    @Bean
    public ButtonService buttonService() {
        return new ButtonService();
    }

    @Bean
    public ReplyKeyboardMarkup replyKeyboardMarkup() {
        return new ReplyKeyboardMarkup();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
