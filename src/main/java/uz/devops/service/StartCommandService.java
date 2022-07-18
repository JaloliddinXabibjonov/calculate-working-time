package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.component.DataLoader;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkerRepository;

@Service(DataLoader.START)
public class StartCommandService extends AbstractBot {

    @Autowired
    private ButtonService buttonService;

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup1 = checkBoss
            ? buttonService.buttons(Status.START, Status.MANAGING)
            : buttonService.buttons(Status.START, Status.START);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1);
        if (replyKeyboardMarkup1.getKeyboard().size() > 0) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup1);
            sendMessage.setText("Assalomu alaykum, botimizga xush kelibsiz! ");
        } else sendMessage.setText("Noma'lum buyruq berildi");
        workerRepository.save(worker);
    }
}
