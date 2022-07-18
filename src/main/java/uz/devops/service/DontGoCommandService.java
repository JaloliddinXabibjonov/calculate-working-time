package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.component.DataLoader;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;

@Service(DataLoader.DONT_GO)
public class DontGoCommandService extends AbstractBot {

    @Autowired
    private ButtonService buttonService;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        sendMessage.setText("Kelolmaslik sababingizni tanlang: ");
        ReplyKeyboardMarkup replyKeyboardMarkup1 = buttonService.buttonsOfReason(Status.ACTIVE);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1);
    }
}
