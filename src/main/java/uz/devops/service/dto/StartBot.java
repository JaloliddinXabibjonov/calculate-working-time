package uz.devops.service.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkerRepository;
import uz.devops.service.ButtonService;

@Service
public class StartBot extends AbstractBot {

    @Autowired
    ButtonService buttonService;

    private final WorkerRepository workerRepository;

    public StartBot(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    @Override
    public void reply(Update update, String message, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        botService(update, worker, checkBoss, sendMessage);
        super.reply(update, message, worker, checkBoss, sendMessage);
    }

    @Override
    public void botService(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup1 = checkBoss
            ? buttonService.buttons(Status.START, Status.MANAGING)
            : buttonService.buttons(Status.START, Status.START);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1);
        if (replyKeyboardMarkup1.getKeyboard().size() > 0) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup1);
        } else sendMessage.setText("Noma'lum buyruq berildi");
        workerRepository.save(worker);
    }
}
