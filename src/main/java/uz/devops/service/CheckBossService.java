package uz.devops.service;

import java.util.List;
import org.springframework.stereotype.Service;
import uz.devops.domain.Worker;
import uz.devops.service.dto.CheckBoss;

@Service
public class CheckBossService {

    public CheckBoss check(List<Worker> workerList) {
        int j = 0;
        boolean check = false;
        CheckBoss checkBoss = new CheckBoss();
        for (int i = 0; i < workerList.size(); i++) {
            if (workerList.get(i).getRole().equals("Boss")) {
                check = true;
            } else if (workerList.get(i).getRole().equals("User")) {
                j = i;
            }
        }

        checkBoss.setIndex(j);
        checkBoss.setCheck(check);
        return checkBoss;
    }
}
