package uz.devops.component;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkerRepository;
import uz.devops.service.WebhookService;

@Component
public class Scheduler {

    @Autowired
    WorkerRepository workerRepository;

    @Autowired
    WebhookService webhookService;
    //    @Scheduled(cron = "00 36 17 * * *")
    //    public void sendReport() {
    //        Set<Worker> allBosses = workerRepository.findAllByRoleAndStatus("Boss", Status.ACTIVE);
    //        for (Worker boss : allBosses) {
    //            webhookService.sendAllTodayWorkHistoryToBoss(boss);
    //        }
    //    }
}
