package uz.devops.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.ActionRepository;
import uz.devops.repository.WorkerRepository;
import uz.devops.service.dto.CheckBoss;
import uz.devops.service.dto.MessageDTo;
import uz.devops.service.dto.StartBot;
import uz.devops.service.dto.WorkerDTO;

@Service
public class TGService {

    private final SendMessage sendMessage = new SendMessage();

    private final WebhookService webhookService;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    CheckBossService checkBossService;

    @Autowired
    WorkerRepository workerRepository;

    @Autowired
    StartBot startBot;

    @Autowired
    ButtonService buttonService;

    public TGService(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    List<MessageDTo> messageDToList = new ArrayList<>();
    int removeIndex = 0;

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
                        switch (command) {
                            case "start":
                                webhookService.startBot(update, "Assalomu alaykum. Botimizga xush kelibsiz!", worker, checkBoss.isCheck());
                                break;
                            case "startWork":
                                webhookService.startWork(update, "Kun davomida yaxshi kayfiyat tilaymiz!", worker, checkBoss.isCheck());
                                break;
                            case "atWork":
                                webhookService.startLunch(update, "Yoqimli ishtaha! ", worker, checkBoss.isCheck());
                                break;
                            case "afterLunch":
                                webhookService.endLunch(update, "Kuningiz barakali o'tsin", worker, checkBoss.isCheck());
                                break;
                            case "goHome":
                                webhookService.workFinished(update, worker, checkBoss.isCheck());
                                break;
                            case "dontGo":
                                messageDToList.add(new MessageDTo(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                                webhookService.dontGo(update);
                                removeIndex = messageDToList.size() - 1;
                                break;
                            case "vacation":
                                webhookService.askDescriptionOfReason(
                                    update,
                                    worker,
                                    "Necha kun dam olmoqchisiz?",
                                    buttonService.descriptionOfReasonButtons()
                                );
                                break;
                            case "disease":
                            case "withReason":
                                messageDToList.add(new MessageDTo(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                                webhookService.askDescriptionOfReason(update, worker, "Ushbu sababni izohlang: (#izoh deb boshlang)");
                                messageDToList.remove(removeIndex);
                                break;
                            case "back":
                                webhookService.startBot(update, "Kerakli bo‘limni tanlang! \uD83D\uDC47", worker, checkBoss.isCheck());
                                break;
                            case "report":
                                webhookService.sendAllTodayWorkHistoryToBoss();
                                break;
                            //                            case "announcement":
                            //                                messageDToList.add(new MessageDTo(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                            //                                webhookService.askAnnouncement(update,check);
                            //                                break;
                            case "addWorker":
                                messageDToList.add(new MessageDTo(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                                webhookService.askWorkerInfo(update, checkBoss.isCheck());
                                break;
                            case "removeWorker":
                                //                                messageDToList.add(new MessageDTo(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                                webhookService.chooseWorkerForRemove(update, checkBoss.isCheck());
                                break;
                            //                               case "removeWorker":
                            //                                messageDToList.add(new MessageDTo(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                            //                                webhookService.askAnnouncement(update,check);
                            //                                break;

                            case "managing":
                                webhookService.managing(update, checkBoss.isCheck());
                                break;
                            default:
                                break;
                        }
                    }
                    if (text.endsWith("kun")) {
                        webhookService.saveDescriptionOfReason(
                            update,
                            worker,
                            "Yuborilgan ma'lumotlar saqlandi. Kerakli bo‘limni tanlang! \uD83D\uDC47",
                            checkBoss.isCheck()
                        );
                    } else if (text.startsWith("#izoh")) {
                        if (messageDToList.size() > 0) {
                            int i = -1;
                            for (MessageDTo messageDTo : messageDToList) {
                                i++;
                                if (
                                    messageDTo.getChatId().equals(update.getMessage().getChatId()) &&
                                    messageDTo.getMessageId() + 2 == update.getMessage().getMessageId()
                                ) {
                                    webhookService.saveDescriptionOfReason(
                                        update,
                                        worker,
                                        "Yuborilgan ma'lumotlar saqlandi. Kerakli bo‘limni tanlang! \uD83D\uDC47",
                                        checkBoss.isCheck()
                                    );
                                    messageDToList.remove(i);
                                    break;
                                }
                            }
                        } else {
                            webhookService.errorCommand(update, "Noto'g'ri buyruq berildi. Kerakli bo‘limni tanlang! \uD83D\uDC47");
                        }
                    } else if (text.startsWith("+worker")) {
                        if (messageDToList.size() > 0) {
                            int i = -1;
                            for (MessageDTo messageDTo : messageDToList) {
                                i++;
                                if (
                                    messageDTo.getChatId().equals(update.getMessage().getChatId()) &&
                                    messageDTo.getMessageId() + 2 == update.getMessage().getMessageId()
                                ) {
                                    text = text.substring(7);
                                    String[] split = text.split(" ");
                                    webhookService.addWorker(
                                        update,
                                        new WorkerDTO(split[0] + " " + split[1], Long.parseLong(split[split.length - 1]), "User")
                                    );
                                    messageDToList.remove(i);
                                    break;
                                }
                            }
                        } else {
                            webhookService.errorCommand(update, "Noto'g'ri buyruq berildi. Kerakli bo‘limni tanlang! \uD83D\uDC47");
                        }
                    }
                } else {
                    webhookService.workerNotFound(update);
                }
            }
        } else if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().startsWith("#removeId")) {
                String removeWorkerTgId = update.getCallbackQuery().getData().substring(9);
                webhookService.removeWorker(update, Long.parseLong(removeWorkerTgId));
            }
        }
    }
}
