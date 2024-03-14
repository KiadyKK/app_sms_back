package org.acme.middleware;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.app_sms_833.Kpi;
import org.acme.model.app_sms_833.User;
import org.acme.model.dm_rf.DwhRes;
import org.acme.repo.app_sms_833.KpiRepo;
import org.acme.repo.app_sms_833.UserRepo;
import org.acme.repo.dm_rf.DwhRepo;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DwhCron {
    @Inject
    Logger LOGGER;

    @Inject
    MailShared mailShared;

    @Inject
    KpiRepo kpiRepo;

    @Inject
    UserRepo userRepo;

    @Inject
    DwhRepo dwhRepo;

    @Scheduled(cron = "0 30 10 * * ?")
    void loadDwhData() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(1);
        String checkData;

        Optional<Kpi> optional = Optional.ofNullable(kpiRepo.getByJour(startDate));
        if (optional.isEmpty()) {
            List<DwhRes> dwhResList = dwhRepo.getAll(startDate, endDate);

            if (!dwhResList.isEmpty()) {
                for (DwhRes dwhRes : dwhResList) {
                    Kpi kpi = new Kpi(dwhRes);
                    kpiRepo.save(kpi);
                }
                checkData = " chargées.";
                LOGGER.info("================================ données chargées");
            } else {
                checkData = " manquantes.";
                LOGGER.info("================================ données manquantes");
            }
        } else {
            checkData = " déjà chargées.";
            LOGGER.info("================================ données déjà chargées");
        }

        List<User> users = userRepo.findAll().stream().toList();
        for (User user : users) {
            mailShared.sendMail(user.getEmail(), startDate, checkData);
        }
    }
}
