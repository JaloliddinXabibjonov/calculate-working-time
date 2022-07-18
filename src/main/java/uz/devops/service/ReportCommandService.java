package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.component.DataLoader;
import uz.devops.domain.Worker;

@Service(DataLoader.REPORT)
public class ReportCommandService extends AbstractBot {

    @Autowired
    private ReportPreparationService reportPreparationService;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        String message = reportPreparationService.prepare();
        sendMessage.setText(message);
    }
}
