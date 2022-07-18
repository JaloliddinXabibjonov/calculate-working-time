package uz.devops.component;

import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uz.devops.domain.Action;
import uz.devops.domain.Reason;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.ActionRepository;
import uz.devops.repository.ReasonRepository;
import uz.devops.repository.WorkerRepository;

@Component
public class DataLoader implements CommandLineRunner {

    public static final String START_WORK = "startWork";
    public static final String END_LUNCH = "endLunch";
    public static final String DONT_GO = "dontGo";
    public static final String GO_HOME = "goHome";
    public static final String START_LUNCH = "atWork";
    public static final String DISEASE = "disease";
    public static final String START = "start";
    public static final String VACATION = "vacation";
    public static final String WITH_REASON = "withReason";
    public static final String BACK = "back";
    public static final String REPORT = "report";
    public static final String ANNOUNCEMENT = "announcement";
    public static final String MANAGING = "managing";
    public static final String ADD_WORKER = "addWorker";
    public static final String REMOVE_WORKER = "removeWorker";

    public static final ZoneId ZONE_ID = ZoneId.of("Asia/Tashkent");

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private ReasonRepository reasonRepository;

    @Value("${spring.sql.init.mode}")
    private String mode;

    @Override
    public void run(String... args) throws Exception {
        if (mode.equals("always")) {
            reasonRepository.save(new Reason(1L, "Kasallik", Status.ACTIVE));
            reasonRepository.save(new Reason(2L, "Ta'til olmoqchiman", Status.ACTIVE));
            reasonRepository.save(new Reason(3L, "Sababli", Status.ACTIVE));
            reasonRepository.save(new Reason(4L, "Orqaga", Status.ACTIVE));

            workerRepository.save(new Worker("Jaloliddin", 573492532L, "User", Status.ACTIVE));
            workerRepository.save(new Worker("Jaloliddin", 5129267392L, "Boss", Status.ACTIVE));

            actionRepository.save(new Action("Ishga keldim", START_WORK, Status.START));
            actionRepository.save(new Action("Borolmayman", DONT_GO, Status.START));
            actionRepository.save(new Action("Uyga ketdim", GO_HOME, Status.GO_HOME));
            actionRepository.save(new Action("Tushlikka ketdim", START_LUNCH, Status.AT_WORK));
            actionRepository.save(new Action("Tushlikdan qaytdim", END_LUNCH, Status.END_LUNCH));
            actionRepository.save(new Action("Kasallik", DISEASE, Status.DISEASE, reasonRepository.getById(1L)));
            actionRepository.save(new Action("/start", START, Status.START_BOT));
            actionRepository.save(new Action("Ta'til olmoqchiman", VACATION, Status.VACATION, reasonRepository.getById(2L)));
            actionRepository.save(new Action("Sababli", WITH_REASON, Status.WITH_REASON, reasonRepository.getById(3L)));
            actionRepository.save(new Action("Orqaga", BACK, Status.BACK, reasonRepository.getById(4L)));
            actionRepository.save(new Action("Hisobot", REPORT, Status.SETTINGS));
            actionRepository.save(new Action("E'lon yuborish", ANNOUNCEMENT, Status.ANNOUNCEMENT));
            actionRepository.save(new Action("Boshqaruv", MANAGING, Status.MANAGING));
            actionRepository.save(new Action("Xodim qo'shish", ADD_WORKER, Status.SETTINGS));
            actionRepository.save(new Action("Xodim o'chirish", REMOVE_WORKER, Status.SETTINGS));
        }
    }
}
