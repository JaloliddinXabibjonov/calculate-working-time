package uz.devops.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.component.DataLoader;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;

@Service(DataLoader.ADD_WORKER)
public class AddWorkerCommandService extends AbstractBot {

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Xodim");
        inlineKeyboardButton1.setCallbackData("worker");
        inlineKeyboardButton1.setText("Boshqaruvchi");
        inlineKeyboardButton1.setCallbackData("manager");
        List<InlineKeyboardButton> inlineKeyboardButtonsRow = new ArrayList<>();
        inlineKeyboardButtonsRow.add(inlineKeyboardButton1);
        List<List<InlineKeyboardButton>> inlineKeyboardButtonRowList = new ArrayList<>();
        inlineKeyboardButtonRowList.add(inlineKeyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtonRowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setText(checkBoss ? "Lavozim tanlang: " : "Noto'g'ri buyruq berildi!");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }
}
