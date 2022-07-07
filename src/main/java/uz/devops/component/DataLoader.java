package uz.devops.component;

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

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    WorkerRepository workerRepository;

    @Autowired
    ReasonRepository reasonRepository;

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

            actionRepository.save(new Action("Ishga keldim", "startWork", Status.START));
            actionRepository.save(new Action("Borolmayman", "dontGo", Status.START));
            actionRepository.save(new Action("Uyga ketdim", "goHome", Status.GO_HOME));
            actionRepository.save(new Action("Tushlikka ketdim", "atWork", Status.AT_WORK));
            actionRepository.save(new Action("Tushlikdan qaytdim", "afterLunch", Status.AFTER_LUNCH));
            actionRepository.save(new Action("Kasallik", "disease", Status.DISEASE, reasonRepository.getById(1L)));
            actionRepository.save(new Action("/start", "start", Status.START_BOT));
            actionRepository.save(new Action("Ta'til olmoqchiman", "vacation", Status.VACATION, reasonRepository.getById(2L)));
            actionRepository.save(new Action("Sababli", "withReason", Status.WITH_REASON, reasonRepository.getById(3L)));
            actionRepository.save(new Action("Orqaga", "back", Status.BACK, reasonRepository.getById(4L)));
            actionRepository.save(new Action("Hisobot", "report", Status.REPORT));
            actionRepository.save(new Action("E'lon yuborish", "announcement", Status.ANNOUNCEMENT));
            actionRepository.save(new Action("Boshqaruv", "managing", Status.MANAGING));
            actionRepository.save(new Action("Xodim qo'shish", "addWorker", Status.SETTINGS));
            actionRepository.save(new Action("Xodim o'chirish", "removeWorker", Status.SETTINGS));
        }
    }
}
