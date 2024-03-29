package uz.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.component.DataLoader;
import uz.devops.domain.WorkHistory;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkHistoryRepository;
import uz.devops.service.dto.WorkingTimeDTO;

import java.time.Instant;
import java.util.Optional;

@Service(DataLoader.GO_HOME)
public class GoHomeCommandService extends AbstractBot {

    @Autowired
    private WorkHistoryRepository workHistoryRepository;

    @Autowired
    private ButtonService buttonService;

    @Autowired
    private WorkingTimeCounterService workingTimeCounterService;

    @Override
    protected void process(Update update, Worker worker, boolean checkBoss, SendMessage sendMessage) {
        Optional<WorkHistory> optionalWorkHistory = workHistoryRepository.findFirstByWorkerIdAndStartIsNotNullAndEndNullOrderByStartDesc(
            worker.getId()
        );
        if (optionalWorkHistory.isPresent()) {
            WorkHistory workHistory = optionalWorkHistory.get();
            workHistory.setWorker(worker);
            workHistory.setEnd(Instant.now());
            workHistory.setStatus(Status.GO_HOME);
            WorkHistory savedWorkHistory = workHistoryRepository.save(workHistory);

            WorkingTimeDTO workingTimeDTO = workingTimeCounterService.counter(savedWorkHistory);
            String hours = workingTimeDTO.getHours() == 0 ? ("") : (workingTimeDTO.getHours() +
                " soat ");
            String minutes = workingTimeDTO.getMinutes() == 0 ? "" : (workingTimeDTO.getMinutes() +
                " minut");
            String work = (hours + minutes).equals("") ? " ishlamadingiz." : " ishladingiz.";
            sendMessage.setText(
                "Bugun siz " +
                    hours + minutes + work +
                    "\nMaroqli hordiq oling"
            );
        } else {
            sendMessage.setText("Siz hali ishga kelmagansiz!");
        }
        ReplyKeyboardMarkup replyKeyboardMarkup1 = checkBoss
            ? buttonService.buttons(Status.START, Status.MANAGING)
            : buttonService.buttons(Status.START);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
    }
}
