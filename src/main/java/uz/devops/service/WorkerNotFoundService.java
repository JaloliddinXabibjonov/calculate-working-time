package uz.devops.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Worker;

@Service
public class WorkerNotFoundService extends AbstractBot {

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        sendMessage.setText("Ro'yxatdan o'tmagansiz!");
    }
}
