package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.devops.component.DataLoader;
import uz.devops.domain.Worker;

@Service(DataLoader.WITH_REASON)
public class WithReasonCommandService extends AbstractBot {

    @Autowired
    private SaveReasonService saveReasonService;

    @Autowired
    private ButtonService buttonService;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        sendMessage.setText("Kelolmaslik sababingizni izohlang: ");
        saveReasonService.saveReason(update, worker);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
    }
}
