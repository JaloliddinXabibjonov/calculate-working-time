package uz.devops.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.config.Constants;
import uz.devops.domain.Reason;
import uz.devops.domain.WorkHistory;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.ActionRepository;
import uz.devops.repository.ReasonRepository;
import uz.devops.repository.WorkHistoryRepository;
import uz.devops.repository.WorkerRepository;
import uz.devops.service.dto.*;
import uz.devops.service.impl.WorkHistoryServiceImpl;
import uz.devops.service.impl.WorkerServiceImpl;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();
    //    private final ZoneId zoneId = ZoneId.of("Asia/Tashkent");
    private final SendMessage sendMessage = new SendMessage();

    private final Logger log = LoggerFactory.getLogger(WebhookService.class);

    @Autowired
    ButtonService buttonService;

    @Autowired
    SendToTelegram sendToTelegram;

    @Autowired
    WorkHistoryRepository workHistoryRepository;

    @Autowired
    ReasonRepository reasonRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    WorkerRepository workerRepository;

    @Autowired
    WorkHistoryServiceImpl workHistoryService;

    @Autowired
    WorkerServiceImpl workerService;

    @Autowired
    StartBot startBot;

    @Autowired
    StartWork startWork;

    @Autowired
    StartLunch startLunch;

    @Autowired
    EndLunch endLunch;

    public void startBot(Update update, String message, Worker worker, boolean checkBoss) {
        startBot.reply(update, message, worker, checkBoss, sendMessage);
    }

    public void startWork(Update update, String message, Worker worker, boolean checkBoss) {
        startWork.reply(update, message, worker, checkBoss, sendMessage);
    }

    public void startLunch(Update update, String message, Worker worker, boolean checkBoss) {
        startLunch.reply(update, message, worker, checkBoss, sendMessage);
    }

    public void endLunch(Update update, String message, Worker worker, boolean checkBoss) {
        endLunch.reply(update, message, worker, checkBoss, sendMessage);
    }

    public void workFinished(Update update, Worker worker, boolean check) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        Optional<WorkHistory> optionalWorkHistory = workHistoryRepository.findFirstByWorkerIdAndStartIsNotNullAndEndNull(worker.getId());
        if (optionalWorkHistory.isPresent()) {
            WorkHistory workHistory = optionalWorkHistory.get();
            workHistory.setWorker(worker);
            workHistory.setEnd(Instant.now());
            workHistory.setStatus(Status.ACTIVE);
            WorkHistory saved = workHistoryRepository.save(workHistory);
            Instant start = saved.getStart();
            Instant toLunch = saved.getToLunch();
            Instant fromLunch = saved.getFromLunch();
            Instant end = saved.getEnd();
            long allMinutes = end.getEpochSecond() / 60 - start.getEpochSecond() / 60;
            long breakMinutes =
                (fromLunch == null ? 0 : fromLunch.getEpochSecond() / 60) - (toLunch == null ? 0 : toLunch.getEpochSecond() / 60);
            long allWorkingMinutes = allMinutes - breakMinutes;
            long workingHours = allWorkingMinutes / 60;
            long workingMinutes = allWorkingMinutes % 60;
            sendMessage.setText("Bugun siz " + workingHours + " soat " + workingMinutes + " minut ishladingiz." + "\nMaroqli hordiq oling");
        } else {
            sendMessage.setText("Siz hali ishga kelmagansiz!");
        }
        ReplyKeyboardMarkup replyKeyboardMarkup1 = check
            ? buttonService.buttons(Status.START, Status.MANAGING)
            : buttonService.buttons(Status.START, Status.START);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
        System.out.println(update.getMessage().toString());
        sendToTelegram();
    }

    private void sendToTelegram() {
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
    }

    public void dontGo(Update update) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText("Kelolmaslik sababingizni tanlang: ");
        ReplyKeyboardMarkup replyKeyboardMarkup1 = buttonService.buttonsOfReason(Status.ACTIVE);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
        System.out.println(update.getMessage().toString());
        sendToTelegram();
    }

    public void askDescriptionOfReason(Update update, Worker worker, String message) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(message);
        savingReason(update, worker);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sendToTelegram();
    }

    public void askDescriptionOfReason(Update update, Worker worker, String message, ReplyKeyboardMarkup replyKeyboardMarkup1) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(message);
        savingReason(update, worker);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1);
        sendToTelegram();
    }

    private void savingReason(Update update, Worker worker) {
        WorkHistory workHistory = new WorkHistory();
        Reason reason = reasonRepository.getByName(update.getMessage().getText());
        workHistory.setReason(reason);
        workHistory.setWorker(worker);
        workHistory.setStart(Instant.now());
        workHistoryRepository.save(workHistory);
    }

    public void saveDescriptionOfReason(Update update, Worker worker, String message, boolean checkBoss) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        Optional<WorkHistory> optionalWorkHistory = workHistoryRepository.findFirstByWorkerIdAndStartIsNotNullAndReasonDescriptionIsNullAndReasonIsNotNullOrderByStart(
            worker.getId()
        );
        if (optionalWorkHistory.isPresent()) {
            WorkHistory workHistory = optionalWorkHistory.get();
            workHistory.setReasonDescription(
                update.getMessage().getText().startsWith("#izoh")
                    ? update.getMessage().getText().substring(5)
                    : update.getMessage().getText()
            );
            workHistoryRepository.save(workHistory);
            sendMessage.setText(message);
        } else {
            sendMessage.setText("Noto'g'ri buyruq berildi!");
        }
        sendMessage.setReplyMarkup(
            checkBoss ? buttonService.buttons(Status.START, Status.MANAGING) : buttonService.buttons(Status.START, Status.START)
        );
        sendToTelegram();
    }

    public void errorCommand(Update update, String message) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(buttonService.buttons(Status.START, Status.START));
        sendToTelegram();
    }

    public void workerNotFound(Update update) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText("Ro'yxatdan o'tmagansiz!");
        sendToTelegram();
    }

    public void sendAllTodayWorkHistoryToBoss() {
        Set<WorkHistory> allToday = workHistoryService.getAllToday();
        String message =
            "Sana: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm")) + " (Toshkent vaqti bilan)\n";
        if (allToday.size() > 0) {
            for (WorkHistory workHistory : allToday) {
                String fullName = workHistory.getWorker().getFullName();
                Instant start = workHistory.getStart();
                Instant toLunch = workHistory.getToLunch();
                Instant fromLunch = workHistory.getFromLunch();
                Instant end = workHistory.getEnd();
                Reason reason = workHistory.getReason();
                if (end == null) {
                    message =
                        message.concat(
                            "<b>Xodim: </b><i>" +
                            fullName +
                            "</i>" +
                            (
                                reason == null
                                    ? "\n<b>Ish davomiyligi:</b> <i> ishlamoqda...</i>\n"
                                    : " <i>,Bugun borolmayman. Sababi: " +
                                    reason.getName() +
                                    ", izoh: " +
                                    workHistory.getReasonDescription() +
                                    "</i>\n"
                            )
                        );
                    continue;
                }
                long allMinutes = end.getEpochSecond() / 60 - start.getEpochSecond() / 60;
                long breakMinutes =
                    (fromLunch == null ? 0 : fromLunch.getEpochSecond() / 60) - (toLunch == null ? 0 : toLunch.getEpochSecond() / 60);
                long allWorkingMinutes = allMinutes - breakMinutes;
                long workingHours = allWorkingMinutes / 60;
                long workingMinutes = allWorkingMinutes % 60;
                message =
                    message.concat(
                        "<b>Xodim: </b><i>" +
                        fullName +
                        "</i>\n<b>Ish davomiyligi: </b> <i>" +
                        workingHours +
                        " soat " +
                        workingMinutes +
                        " minut</i>\n"
                    );
            }
            sendMessage.setText(message);
        } else sendMessage.setText("Ma'lumot topilmadi!");
        sendToTelegram();
    }

    public void managing(Update update, boolean check) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(check ? "Kerakli bo‘limni tanlang! \uD83D\uDC47" : "Noto'g'ri buyruq berildi!");
        sendMessage.setReplyMarkup(buttonService.buttons(Status.SETTINGS, Status.BACK));
        sendToTelegram();
    }

    public void askWorkerInfo(Update update, boolean check) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(
            check
                ? "Xodim ma'lumotlarini kiriting(familiyasi ismi telegram id si): (+worker deb boshlang,\n Mas: +workerSodiqov Sodiq 573492532)"
                : "Noto'g'ri buyruq berildi!"
        );
        sendMessage.setReplyMarkup(buttonService.buttons(Status.SETTINGS, Status.BACK));
        sendToTelegram();
    }

    public void chooseWorkerForRemove(Update update, boolean check) {
        Set<Worker> workers = workerRepository.findAllByRoleAndStatus("User", Status.ACTIVE);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardButtonRowList = new ArrayList<>();
        for (Worker worker : workers) {
            List<InlineKeyboardButton> inlineKeyboardButtonsRow = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText(worker.getFullName());
            inlineKeyboardButton1.setCallbackData("#removeId" + worker.getWorkerTgId());
            inlineKeyboardButtonsRow.add(inlineKeyboardButton1);
            inlineKeyboardButtonRowList.add(inlineKeyboardButtonsRow);
        }
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtonRowList);
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(check ? "O'chiriladigan xodimni tanlang: " : "Noto'g'ri buyruq berildi!");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendToTelegram();
    }

    public void removeWorker(Update update, Long removeWorkerTgId) {
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        sendMessage.setText("Xodim muvaffaqiyatli o'chirildi. Kerakli bo‘limni tanlang! \uD83D\uDC47");
        sendMessage.setReplyMarkup(buttonService.buttons(Status.SETTINGS, Status.BACK));
        Worker worker = workerRepository.getByWorkerTgId(removeWorkerTgId);
        worker.setStatus(Status.DELETED);
        workerRepository.save(worker);
        sendToTelegram();
    }

    public void addWorker(Update update, WorkerDTO workerDTO) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setReplyMarkup(buttonService.buttons(Status.SETTINGS, Status.BACK));
        workerService.save(workerDTO);
        sendMessage.setText("Xodim muvaffaqiyatli qo'shildi. Kerakli bo‘limni tanlang! \uD83D\uDC47");
        sendToTelegram();
    }
    //    public void askAnnouncement(Update update, boolean checkBoss) {
    //        SendMessage sendMessage = new SendMessage();
    //        sendMessage.setChatId(update.getMessage().getChatId().toString());
    //        sendMessage.setText(checkBoss?"E'lon matnini yuboring: (#elon deb boshlang)":"Noto'g'ri buyruq berildi!");
    //        restTemplate.postForObject(Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage", sendMessage, ResultTelegram.class);
    //    }

    //    public void sendAnnouncement(Update update, Set<Long> workersChatIds, String announcement) {
    //        SendMessage sendMessage = new SendMessage();
    //        for (Long chatId : workersChatIds) {
    //            sendMessage.setChatId(chatId.toString());
    //            sendMessage.setText(announcement);
    //            restTemplate.postForObject(Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage", sendMessage, ResultTelegram.class);
    //        }
    //        sendMessage.setChatId(update.getMessage().getChatId().toString());
    //        sendMessage.setText("E'lon yuborildi!");
    //        restTemplate.postForObject(Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage", sendMessage, ResultTelegram.class);
    //    }

}
