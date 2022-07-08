package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.component.DataLoader;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;

@Service(DataLoader.MANAGING)
public class ManagingCommandService extends AbstractBot {

    @Autowired
    private ButtonService buttonService;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        sendMessage.setText(checkBoss ? "Kerakli bo‘limni tanlang! \uD83D\uDC47" : "Noto'g'ri buyruq berildi!");
        sendMessage.setReplyMarkup(buttonService.buttons(Status.SETTINGS, Status.BACK));
    }
}
