package uz.devops.web;

import java.net.MalformedURLException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.service.TGService;

@RestController
@RequestMapping("/api/telegram")
public class TGBotResource {

    private final TGService tgService;

    public TGBotResource(TGService tgService) {
        this.tgService = tgService;
    }

    @PostMapping
    public void entry(@RequestBody Update update) throws MalformedURLException {
        //TELEGRAMDAN KELGAN OBJECTNI O`ZIMIZNI METODGA BERIB YUBORDIK
        tgService.enterUpdate(update);
    }
}
