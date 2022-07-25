package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkerRepository;

@Service
public class AddManager extends AbstractBot {

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        String text = update.getMessage().getText();
        text = text.substring(7);
        String[] split = text.split(" ");
        workerRepository.save(
            new Worker(split[0].trim() + " " + split[1].trim(), Long.parseLong(split[split.length - 1]), "Boss", Status.ACTIVE)
        );
        sendMessage.setText("Xodim muvaffaqiyatli qo'shildi!");
    }

    @Override
    public void reply(Long chatId, String message) {
        super.reply(
            chatId,
            "Boshqaruvchi ma'lumotlarini kiriting(familiyasi ismi telegram id si): (+manager deb boshlang,\n Mas: +managerSodiqov Sodiq 573492532)"
        );
    }
}
