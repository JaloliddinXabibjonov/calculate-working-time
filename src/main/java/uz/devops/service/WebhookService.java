package uz.devops.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.config.BotProperties;
import uz.devops.config.Constants;
import uz.devops.domain.Action;
import uz.devops.domain.Reason;
import uz.devops.domain.WorkHistory;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.ActionRepository;
import uz.devops.repository.ReasonRepository;
import uz.devops.repository.WorkHistoryRepository;
import uz.devops.repository.WorkerRepository;
import uz.devops.service.dto.ResultTelegram;
import uz.devops.service.impl.WorkHistoryServiceImpl;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    private final ZoneId zoneId = ZoneId.of("Asia/Tashkent");
    private final SendMessage sendMessage = new SendMessage();

    private static final BotProperties botProperties = new BotProperties();

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

    private static final String sendMessageUrl = botProperties.getBotUrl() + botProperties.getToken() + "/sendMessage";

    public void startBot(Update update, String message, Worker worker, boolean checkBoss) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        ReplyKeyboardMarkup replyKeyboardMarkup1 = checkBoss ? buttons(Status.START, Status.MANAGING) : buttons(Status.START, Status.START);
        if (replyKeyboardMarkup1.getKeyboard().size() > 0) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup1);
            sendMessage.setText(message);
        } else sendMessage.setText("Noma'lum buyruq berildi");
        worker.setChatId(update.getMessage().getChatId());
        workerRepository.save(worker);
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
        System.out.println(update.getMessage().toString());
    }

    public void startWork(Update update, Worker worker, boolean checkBoss) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        boolean exists = workHistoryRepository.existsByWorkerIdAndStartIsNotNullAndEndIsNullAndReasonIsNotNullAndStartBetween(
            worker.getId(),
            LocalDate.now().atStartOfDay(zoneId).toInstant(),
            LocalDate.now().atStartOfDay(zoneId).plusSeconds(86400).toInstant()
        );
        if (!exists) {
            workHistoryRepository.save(new WorkHistory(Instant.now(), Status.ACTIVE, worker));
            sendMessage.setText("Kun davomida yaxshi kayfiyat tilaymiz");
        } else sendMessage.setText("Siz allaqachon ishga kelgansiz!");
        ReplyKeyboardMarkup replyKeyboardMarkup1 = checkBoss
            ? buttons(Status.AT_WORK, Status.GO_HOME, Status.MANAGING)
            : buttons(Status.AT_WORK, Status.GO_HOME);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
        System.out.println(update.getMessage().toString());
    }

    public void startLunch(Update update, Worker worker, boolean checkBoss) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        Optional<WorkHistory> optionalWorkHistory = workHistoryRepository.findFirstByWorkerIdAndStartIsNotNullAndStartBetweenOrderByStartDesc(
            worker.getId(),
            LocalDate.now().atStartOfDay(zoneId).toInstant(),
            LocalDate.now().atStartOfDay(zoneId).plusSeconds(86400).toInstant()
        );
        if (optionalWorkHistory.isPresent()) {
            WorkHistory workHistory = optionalWorkHistory.get();
            workHistory.setStatus(Status.ACTIVE);
            workHistory.setWorker(worker);
            workHistory.setToLunch(Instant.now());
            workHistoryRepository.save(workHistory);
            sendMessage.setText("Yoqimli ishtaha");
        } else sendMessage.setText("Siz hali ishga  kelmagansiz!");
        ReplyKeyboardMarkup replyKeyboardMarkup1 = checkBoss
            ? buttons(Status.AFTER_LUNCH, Status.MANAGING)
            : buttons(Status.AFTER_LUNCH, Status.AFTER_LUNCH);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
        System.out.println(update.getMessage().toString());
    }

    public void endLunch(Update update, Worker worker, boolean checkBoss) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        Optional<WorkHistory> optionalWorkHistory = workHistoryRepository.findTopByWorkerIdAndStartIsNotNullAndToLunchIsNotNullAndStartBetweenOrderByStartDesc(
            worker.getId(),
            LocalDate.now().atStartOfDay(zoneId).toInstant(),
            LocalDate.now().atStartOfDay(zoneId).plusSeconds(86400).toInstant()
        );
        if (optionalWorkHistory.isPresent()) {
            WorkHistory workHistory = optionalWorkHistory.get();
            workHistory.setStatus(Status.ACTIVE);
            workHistory.setWorker(worker);
            workHistory.setFromLunch(Instant.now());
            workHistoryRepository.save(workHistory);
            sendMessage.setText("Kuningiz barakali o'tsin");
        } else sendMessage.setText("Siz hali tushlikka chiqmagansiz!");
        ReplyKeyboardMarkup replyKeyboardMarkup1 = checkBoss
            ? buttons(Status.GO_HOME, Status.MANAGING)
            : buttons(Status.GO_HOME, Status.GO_HOME);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
        System.out.println(update.getMessage().toString());
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
        ReplyKeyboardMarkup replyKeyboardMarkup1 = check ? buttons(Status.START, Status.MANAGING) : buttons(Status.START, Status.START);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
        System.out.println(update.getMessage().toString());
    }

    public void dontGo(Update update) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText("Kelolmaslik sababingizni tanlang: ");
        ReplyKeyboardMarkup replyKeyboardMarkup1 = buttonsOfReason(Status.ACTIVE);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1.getKeyboard().size() > 0 ? replyKeyboardMarkup1 : null);
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendmessage",
            sendMessage,
            ResultTelegram.class
        );
        System.out.println(update.getMessage().toString());
    }

    public void askDescriptionOfReason(Update update, Worker worker, String message) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(message);
        savingReason(update, worker);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
    }

    public void askDescriptionOfReason(Update update, Worker worker, String message, ReplyKeyboardMarkup replyKeyboardMarkup1) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(message);
        savingReason(update, worker);
        sendMessage.setReplyMarkup(replyKeyboardMarkup1);
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
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
        sendMessage.setReplyMarkup(checkBoss ? buttons(Status.START, Status.MANAGING) : buttons(Status.START, Status.START));
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
    }

    public void errorCommand(Update update, String message) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(buttons(Status.START, Status.START));
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
    }

    public void workerNotFound(Update update) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText("Ro'yxatdan o'tmagansiz!");
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
    }

    public void sendAllTodayWorkHistoryToBoss(Worker worker) {
        sendMessage.enableHtml(true);
        sendMessage.setChatId(worker.getChatId().toString());
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
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
    }

    public void managing(Update update, boolean check) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(check ? "Kerakli bo‘limni tanlang! \uD83D\uDC47" : "Noto'g'ri buyruq berildi!");
        sendMessage.setReplyMarkup(buttons(Status.SETTINGS, Status.BACK));
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
    }

    public void askWorkerInfo(Update update, boolean check) {
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(
            check
                ? "Xodim ma'lumotlarini kiriting(familiyasi ismi telegram id si): (+worker deb boshlang,\n Mas: Sodiqov Sodiq 573492532)"
                : "Noto'g'ri buyruq berildi!"
        );
        sendMessage.setReplyMarkup(buttons(Status.SETTINGS, Status.BACK));
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
    }

    public void chooseWorkerForRemove(Update update, boolean check) {
        Set<Worker> workers = workerRepository.findAllByRole("User");
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
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(check ? "O'chiriladigan xodimni tanlang: " : "Noto'g'ri buyruq berildi!");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
    }

    public void removeWorker(Update update, Long removeWorkerTgId) {
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        workerRepository.deleteById(removeWorkerTgId);
        sendMessage.setText("Xodim muvaffaqiyatli o'chirildi. Kerakli bo‘limni tanlang! \uD83D\uDC47");
        sendMessage.setReplyMarkup(buttons(Status.SETTINGS, Status.BACK));
        restTemplate.postForObject(
            Constants.TELEGRAM_BOT_URL + Constants.TELEGRAM_BOT_TOKEN + "/sendMessage",
            sendMessage,
            ResultTelegram.class
        );
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

    public ReplyKeyboardMarkup buttonsOfReason(Status status) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboard);
        for (Reason reason : reasonRepository.getAllByStatus(status)) {
            KeyboardRow row = new KeyboardRow();
            row.add(reason.getName());
            keyboard.add(row);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup buttons(Status status, Status status1) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboard);
        for (Action action : actionRepository.getActionsByStatusOrStatus(status, status1)) {
            KeyboardRow row = new KeyboardRow();
            row.add(action.getName());
            keyboard.add(row);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    ReplyKeyboardMarkup buttons(Status status) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboard);
        for (Action action : actionRepository.getActionsByStatus(status)) {
            KeyboardRow row = new KeyboardRow();
            row.add(action.getName());
            keyboard.add(row);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup buttons(Status status, Status status1, Status status2) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboard);
        for (Action action : actionRepository.getActionsByStatusOrStatusOrStatus(status, status1, status2)) {
            KeyboardRow row = new KeyboardRow();
            row.add(action.getName());
            keyboard.add(row);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup descriptionOfReasonButtons() {
        List<KeyboardRow> keyboard = new ArrayList<>();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboard);
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow1.add("1 kun");
        keyboardRow1.add("2 kun");
        keyboardRow2.add("4 kun");
        keyboardRow1.add("3 kun");
        keyboardRow2.add("5 kun");
        keyboardRow2.add("6 kun");
        keyboard.add(keyboardRow1);
        keyboard.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
