package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.middleware.MailShared;
import org.acme.middleware.SmsPoster;
import org.acme.model.app_sms_833.Kpi;
import org.acme.model.app_sms_833.User;
import org.acme.model.dm_rf.DwhRes;
import org.acme.model.dm_rf.Zone;
import org.acme.repo.app_sms_833.KpiRepo;
import org.acme.repo.app_sms_833.UserRepo;
import org.acme.repo.dm_rf.DwhRepo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class KpiService {
    @ConfigProperty(name = "app.name")
    private String APP_NAME;

    @Inject
    Logger LOGGER;

    @Inject
    MailShared mailShared;

    @Inject
    UserRepo userRepo;

    @Inject
    KpiRepo kpiRepo;

    @Inject
    DwhRepo dwhRepo;

    public Response getDwh() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(1);
        String checkData;

        Optional<Kpi> optional = Optional.ofNullable(kpiRepo.getByJour(startDate));
//        if (optional.isEmpty()) {
//            List<DwhRes> dwhResList = dwhRepo.getAll(startDate, endDate);
//
//            if (!dwhResList.isEmpty()) {
//                for (DwhRes dwhRes : dwhResList) {
//                    Kpi kpi = new Kpi(dwhRes);
//                    kpiRepo.save(kpi);
//                }
//                checkData = " chargées.";
//                LOGGER.info("================================ données chargées");
//            } else {
//                checkData = " manquantes.";
//                LOGGER.info("================================ données manquantes");
//            }
//        } else {
//            checkData = " déjà chargées.";
//            LOGGER.info("================================ données déjà chargées");
//        }

        List<User> users = userRepo.findAll().stream().toList();
        System.out.println("==========size " + users.size());
        for (User user : users) {
            mailShared.sendMail(user.getEmail(), startDate, " chargées");
            System.out.println("========================= mail send");
        }
        return Response.noContent().build();
    }

    public Response getAll() {
        List<Kpi> kpis = kpiRepo.getAll();
        return Response.ok(kpis).build();
    }

    public Response sendSms() {
        SmsPoster.sendSMSHttp(APP_NAME, "0320752487", "test kpi");
        return Response.ok().build();
    }

    public Response getZone() {
        List<Zone> zones = dwhRepo.getAllZone();
        return Response.ok(zones).build();
    }
}
