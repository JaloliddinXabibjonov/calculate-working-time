package uz.devops.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "uz-Latn-uz";

    public static final String TELEGRAM_BOT_URL = "https://api.telegram.org/bot";
    //    public static final String TELEGRAM_BOT_URL_WITHOUT_BOT="https://api.telegram.org/";
    public static final String TELEGRAM_BOT_TOKEN = "5570415388:AAHde-Ypp1o9yF2Jr2zwOXHi_jer66PMVJg";

    //    public static final String TELEGRAM_BOT_MY_URL="https://dc76-83-221-180-161.in.ngrok.io/";
    private Constants() {}
}
