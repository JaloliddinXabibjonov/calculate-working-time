package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.component.DataLoader;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;

@Service(DataLoader.ADD_WORKER)
public class AddWorkerCommandService extends AbstractBot {

    @Autowired
    private ButtonService buttonService;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        sendMessage.setText(
            checkBoss
                ? "Xodim ma'lumotlarini kiriting(familiyasi ismi telegram id si): (+worker deb boshlang,\n Mas: +workerSodiqov Sodiq 573492532)"
                : "Noto'g'ri buyruq berildi!"
        );
        sendMessage.setReplyMarkup(buttonService.buttons(Status.SETTINGS, Status.BACK));
    }
}
