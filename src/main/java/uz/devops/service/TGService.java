package uz.devops.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.domain.Worker;
import uz.devops.repository.ActionRepository;
import uz.devops.repository.WorkerRepository;
import uz.devops.service.dto.MessageDTo;

@Service
public class TGService {

    private final WebhookService webhookService;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    WorkerRepository workerRepository;

    public TGService(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    List<MessageDTo> messageDToList = new ArrayList<>();
    int removeIndex = 0;

    public void enterUpdate(Update update) {
        boolean check = false;
        int j = 0;
        if (update.hasMessage()) {
            List<Worker> optionalWorker = workerRepository.findAllByWorkerTgId(update.getMessage().getFrom().getId());
            if (optionalWorker.size() > 0) {
                for (int i = 0; i < optionalWorker.size(); i++) {
                    if (optionalWorker.get(i).getRole().equals("Boss")) {
                        j = i;
                        check = true;
                    }
                }
                Worker worker = optionalWorker.get(check ? j : 0);
                if (update.getMessage().hasText()) {
                    String text = update.getMessage().getText();
                    String command = actionRepository.getCommandByName(text);
                    if (command != null) {
                        switch (command) {
                            case "start":
                                webhookService.startBot(update, "Assalomu alaykum. Botimizga xush kelibsiz!", worker, check);
                                break;
                            case "startWork":
                                webhookService.startWork(update, worker, check);
                                break;
                            case "atWork":
                                webhookService.startLunch(update, worker, check);
                                break;
                            case "afterLunch":
                                webhookService.endLunch(update, worker, check);
                                break;
                            case "goHome":
                                webhookService.workFinished(update, worker, check);
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
                                    webhookService.descriptionOfReasonButtons()
                                );
                                break;
                            case "disease":
                            case "withReason":
                                messageDToList.add(new MessageDTo(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                                webhookService.askDescriptionOfReason(update, worker, "Ushbu sababni izohlang: (#izoh deb boshlang)");
                                messageDToList.remove(removeIndex);
                                break;
                            case "back":
                                int i = 0;
                                for (MessageDTo messageDTo : messageDToList) {
                                    if (
                                        messageDTo.getChatId().equals(update.getMessage().getChatId()) &&
                                        messageDTo.getMessageId() + 2 == update.getMessage().getMessageId()
                                    ) {
                                        webhookService.startBot(update, "Kerakli bo‘limni tanlang! \uD83D\uDC47", worker, check);
                                        messageDToList.remove(i);
                                    }
                                    i++;
                                }
                                break;
                            case "report":
                                webhookService.sendAllTodayWorkHistoryToBoss(worker);
                                break;
                            //                            case "announcement":
                            //                                messageDToList.add(new MessageDTo(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                            //                                webhookService.askAnnouncement(update,check);
                            //                                break;
                            case "addWorker":
                                messageDToList.add(new MessageDTo(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                                webhookService.askWorkerInfo(update, check);
                                break;
                            case "removeWorker":
                                messageDToList.add(new MessageDTo(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                                webhookService.chooseWorkerForRemove(update, check);
                                break;
                            //                               case "removeWorker":
                            //                                messageDToList.add(new MessageDTo(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                            //                                webhookService.askAnnouncement(update,check);
                            //                                break;

                            case "managing":
                                webhookService.managing(update, check);
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
                            check
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
                                        check
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
        }
        //        else if (update.hasCallbackQuery() ) {
        //            if (update.getCallbackQuery().getData().startsWith("#removeId")) {
        //                String removeWorkerTgId = update.getCallbackQuery().getData().substring(9);
        //                if (messageDToList.size() > 0 && check) {
        //                    int i = -1;
        //                    for (MessageDTo messageDTo : messageDToList) {
        //                        i++;
        //                        if (messageDTo.getChatId().equals(update.getCallbackQuery().getMessage().getChatId()) && messageDTo.getMessageId() + 3 == update.getCallbackQuery().getMessage().getMessageId()) {
        //                            webhookService.removeWorker(update, Long.parseLong(removeWorkerTgId));
        //                            messageDToList.remove(i);
        //                            break;
        //                        }
        //                    }
        //                }
        //            }
        //        }
    }
}
