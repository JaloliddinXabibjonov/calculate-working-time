package uz.devops.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.ActionRepository;
import uz.devops.repository.WorkerRepository;
import uz.devops.service.dto.*;

@Service
public class TGService {

    private final Logger log = LoggerFactory.getLogger(TGService.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private CheckBossService checkBossService;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private WorkerNotFoundService workerNotFoundService;

    @Autowired
    private RemoveWorker removeWorker;

    @Autowired
    private AddWorker addWorker;

    @Autowired
    private SaveDescriptionService saveDescriptionService;

    @Autowired
    private SaveVacationDayService saveVacationDayService;

    @Autowired
    private AddManager addManager;

    public void enterUpdate(Update update) {
        if (update.hasMessage()) {
            List<Worker> workerList = workerRepository.findAllByWorkerTgIdAndStatus(update.getMessage().getFrom().getId(), Status.ACTIVE);
            if (workerList.size() > 0) {
                CheckBoss checkBoss = checkBossService.check(workerList);
                Worker worker = workerList.get(checkBoss.getIndex());
                if (update.getMessage().hasText()) {
                    String text = update.getMessage().getText();
                    String command = actionRepository.getCommandByName(text);
                    if (command != null) {
                        if (applicationContext.containsBeanDefinition(command)) {
                            try {
                                final AbstractBot bean = applicationContext.getBean(command, AbstractBot.class);
                                bean.reply(update, worker, checkBoss.isCheck());
                                return;
                            } catch (Exception e) {
                                log.warn("Error on searching command service : " + e.getLocalizedMessage());
                                return;
                            }
                        }
                    }

                    if (text.endsWith("kun")) {
                        saveVacationDayService.reply(update, worker, checkBoss.isCheck());
                    } else if (text.startsWith("#izoh")) {
                        saveDescriptionService.reply(update, worker, checkBoss.isCheck());
                    } else if (text.startsWith("+worker")) {
                        addWorker.reply(update, null, false);
                    } else if (text.startsWith("+manager")) {
                        addManager.reply(update, null, false);
                    }
                } else if (update.getMessage().hasAudio()) {
                    workerNotFoundService.reply(update.getMessage().getChatId(), "Noma'lum buyruq!");
                }
            } else {
                workerNotFoundService.reply(update, null, false);
            }
        } else if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().startsWith("#removeId")) {
                removeWorker.reply(update, null, false, true);
            } else if (update.getCallbackQuery().getData().equals("manager")) {
                addManager.reply(update.getCallbackQuery().getMessage().getChatId(), "");
            } else if (update.getCallbackQuery().getData().equals("worker")) {
                addWorker.reply(update.getCallbackQuery().getMessage().getChatId(), "");
            }
        }
    }
}
