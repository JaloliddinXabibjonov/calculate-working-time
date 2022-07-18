package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkerRepository;

@Service("workerRemove")
public class RemoveWorker extends AbstractBot {

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        String removeWorkerTgId = update.getCallbackQuery().getData().substring(9);
        Worker worker1 = workerRepository.getByWorkerTgIdAndRole(Long.valueOf(removeWorkerTgId), "User");
        worker1.setStatus(Status.DELETED);
        workerRepository.save(worker1);
        sendMessage.setText("Xodim muvaffaqiyatli o'chirildi");
    }
}
