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
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Xodim");
        inlineKeyboardButton1.setCallbackData("worker");
        inlineKeyboardButton2.setText("Boshqaruvchi");
        inlineKeyboardButton2.setCallbackData("manager");
        List<InlineKeyboardButton> inlineKeyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonsRow2 = new ArrayList<>();
        inlineKeyboardButtonsRow1.add(inlineKeyboardButton1);
        inlineKeyboardButtonsRow2.add(inlineKeyboardButton2);
        List<List<InlineKeyboardButton>> inlineKeyboardButtonRowList = new ArrayList<>();
        inlineKeyboardButtonRowList.add(inlineKeyboardButtonsRow1);
        inlineKeyboardButtonRowList.add(inlineKeyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtonRowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setText(checkBoss ? "Lavozim tanlang: " : "Noto'g'ri buyruq berildi!");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }
}
