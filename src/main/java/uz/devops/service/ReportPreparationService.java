package uz.devops.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.devops.domain.Reason;
import uz.devops.domain.WorkHistory;
import uz.devops.service.dto.WorkingTimeDTO;
import uz.devops.service.impl.WorkHistoryServiceImpl;

@Service
public class ReportPreparationService {

    @Autowired
    private WorkHistoryServiceImpl workHistoryService;

    @Autowired
    private WorkingTimeCounterService workingTimeCounterService;

    public String prepare() {
        Set<WorkHistory> allToday = workHistoryService.getAllToday();
        String message =
            "Sana: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm")) + " (Toshkent vaqti bilan)\n";
        if (allToday.size() > 0) {
            for (WorkHistory workHistory : allToday) {
                String fullName = workHistory.getWorker().getFullName();

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
                WorkingTimeDTO workingTimeDTO = workingTimeCounterService.counter(workHistory);
                message =
                    message.concat(
                        "<b>Xodim: </b><i>" +
                        fullName +
                        "</i>\n<b>Ish davomiyligi: </b> <i>" +
                        workingTimeDTO.getHours() +
                        " soat " +
                        workingTimeDTO.getMinutes() +
                        " minut</i>\n"
                    );
            }
            return message;
        }
        return "Ma'lumot topilmadi!";
    }
}
