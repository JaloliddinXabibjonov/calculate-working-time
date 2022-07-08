package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkerRepository;

@Service(value = "workerAdd")
public class AddWorker extends AbstractBot {

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        String text = update.getMessage().getText();
        text = text.substring(7);
        String[] split = text.split(" ");
        workerRepository.save(
            new Worker(split[0].trim() + " " + split[1].trim(), Long.parseLong(split[split.length - 1]), "User", Status.ACTIVE)
        );
        sendMessage.setText("Xodim muvaffaqiyatli qo'shildi!");
    }
}
