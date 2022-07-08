package uz.devops.component;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkerRepository;
import uz.devops.service.AbstractBot;
import uz.devops.service.ReportPreparationService;

@Component
public class Scheduler extends AbstractBot {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private ReportPreparationService reportPreparationService;

    @Override
    public void reply(Long chatId, String message) {
        super.reply(chatId, message);
    }

    @Scheduled(cron = "00 45 15 * * *")
    public void sendReport() {
        Set<Worker> allBosses = workerRepository.findAllByRoleAndStatus("Boss", Status.ACTIVE);
        for (Worker boss : allBosses) {
            String report = reportPreparationService.prepare();
            reply(boss.getWorkerTgId(), report);
        }
    }

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {}
}
