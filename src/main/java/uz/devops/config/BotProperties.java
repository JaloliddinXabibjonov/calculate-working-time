package uz.devops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot")
public class BotProperties {

    private String token;
    private String botUrl;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBotUrl() {
        return botUrl;
    }

    public void setBotUrl(String botUrl) {
        this.botUrl = botUrl;
    }
}
