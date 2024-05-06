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
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    @Scheduled(cron = "{dwh.expr.job}")
    void loadDwhData() {
        LocalDate date = LocalDate.now().minusDays(1);
        List<User> users = userRepo.findAll().stream().toList();

        AtomicInteger i = new AtomicInteger();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            i.getAndIncrement();
            boolean check = startCron(date);
            if (!check || i.get() == 10) {
                String message;
                if (i.get() == 10) message = " non chargées après 10 tentatives !";
                else message = " chargées !";
                for (User user : users) {
                    mailShared.sendMail(user.getEmail(), date, message);
                }
                executor.shutdown();
                LOGGER.info("Données du " + date + message);
            } else
                LOGGER.info("Données du " + date + " indisponibles !");
        };
        executor.scheduleAtFixedRate(task, 0, 3600, TimeUnit.SECONDS);
    }

    private boolean startCron(LocalDate date) {
        List<DwhRes> dwhResList = dwhRepo.getAll(date);
        Map<LocalDate, List<DwhRes>> dwhResListGrouped = dwhResList.stream().collect(Collectors.groupingBy(DwhRes::getJour));
        boolean check = false;

        for (LocalDate jour : dwhResListGrouped.keySet()) {
            check = dwhResListGrouped.get(jour).stream().allMatch(dwhRes -> dwhRes.getParc() == 0);
            if (check) break;
        }

        if (!check) {
            //Remove all data before persist
            kpiRepo.removeAll(date);

            for (DwhRes dwhRes : dwhResList) {
                Kpi kpi = new Kpi(dwhRes);
                kpiRepo.save(kpi);
            }
        }

        return check;
    }
}
