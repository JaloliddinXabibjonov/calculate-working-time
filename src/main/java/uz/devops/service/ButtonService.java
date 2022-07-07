package uz.devops.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.domain.Action;
import uz.devops.domain.Reason;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.ActionRepository;
import uz.devops.repository.ReasonRepository;

@Service
public class ButtonService {

    @Autowired
    ReasonRepository reasonRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    ReplyKeyboardMarkup replyKeyboardMarkup;

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
