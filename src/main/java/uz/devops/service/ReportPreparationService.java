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
            "Vaqt: " +
            LocalDateTime.now().plusHours(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm")) +
            " (Toshkent vaqti bilan)\n";
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
                String hours = workingTimeDTO.getHours() == 0 ? ("") : (workingTimeDTO.getHours() +
                    " soat ");
                String minutes = workingTimeDTO.getMinutes() == 0 ? "0 minut" : (workingTimeDTO.getMinutes() +
                    " minut");
                message =
                    message.concat(
                        "<b>Xodim: </b><i>" +
                        fullName +
                        "</i>\n<b>Ish davomiyligi: </b> <i>" +
                        hours +
                        minutes+
                        " </i>\n"
                    );
            }
            return message;
        }
        return "Ma'lumot topilmadi!";
    }
}
