package uz.devops.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.component.DataLoader;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkerRepository;

@Service(DataLoader.REMOVE_WORKER)
public class RemoveWorkerCommandService extends AbstractBot {

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        Set<Worker> workers = workerRepository.findAllByRoleAndStatus("User", Status.ACTIVE);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardButtonRowList = new ArrayList<>();
        for (Worker worker1 : workers) {
            List<InlineKeyboardButton> inlineKeyboardButtonsRow = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText(worker1.getFullName());
            inlineKeyboardButton1.setCallbackData("#removeId" + worker1.getWorkerTgId());
            inlineKeyboardButtonsRow.add(inlineKeyboardButton1);
            inlineKeyboardButtonRowList.add(inlineKeyboardButtonsRow);
        }
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtonRowList);
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        int size = inlineKeyboardMarkup.getKeyboard().size();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setText(checkBoss && size > 0 ? "O'chiriladigan xodimni tanlang: " : "Xodim topilmadi!");
    }
}
