package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.middleware.HttpClientService;
import org.acme.middleware.MailShared;
import org.acme.middleware.SmsProxy;
import org.acme.model.app_sms_833.Historic;
import org.acme.model.app_sms_833.Kpi;
import org.acme.model.app_sms_833.Rdz;
import org.acme.model.app_sms_833.User;
import org.acme.model.dm_rf.DwhRes;
import org.acme.model.dm_rf.Zone;
import org.acme.repo.app_sms_833.HistoricRepo;
import org.acme.repo.app_sms_833.KpiRepo;
import org.acme.repo.app_sms_833.RdzRepo;
import org.acme.repo.app_sms_833.UserRepo;
import org.acme.repo.dm_rf.DwhRepo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    RdzRepo rdzRepo;

    @Inject
    KpiRepo kpiRepo;

    @Inject
    HistoricRepo historicRepo;

    @Inject
    DwhRepo dwhRepo;

    @RestClient
    SmsProxy smsProxy;

    @Inject
    HttpClientService httpClientService;

    public Response testSms(String msisdn) throws UnsupportedEncodingException {
        Kpi kpi = kpiRepo.listAll().stream().findFirst().get();
        String message = getString(kpi);

        String url = "http://10.249.248.40:80/cgi-bin/sendsms?username=smsgw&password=mypass&from=" + APP_NAME + "&to=" + msisdn + "&text=" + URLEncoder.encode(message, "UTF-8");
        httpClientService.get(url);

        return Response.noContent().build();
    }

    public Response getAll(String date) {
        List<Kpi> kpis = kpiRepo.getAll(LocalDate.parse(date));
        return Response.ok(kpis).build();
    }

    public Response getAllDwh(String searchDate) {
        LocalDate date = LocalDate.parse(searchDate);
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
        } else {
            dwhResList = new ArrayList<>();
        }

        return Response.ok(dwhResList).build();
    }

    public Response sendSms(String date, String tri) throws UnsupportedEncodingException {
        LocalDate startDate = LocalDate.parse(date).minusDays(1);
        LocalDate endDate = startDate.plusDays(1);
        List<Rdz> rdzs = rdzRepo.getAll("");
        List<Kpi> kpis = kpiRepo.getAll(LocalDate.parse(date));
        for (Kpi kpi : kpis) {
            String message = getString(kpi);
            for (Rdz rdz : rdzs) {
                if (kpi.getZone().equals(rdz.getZone())) {
                    String url = "http://10.249.248.40:80/cgi-bin/sendsms?username=smsgw&password=mypass&from=" + APP_NAME + "&to=" + rdz.getTel() + "&text=" + URLEncoder.encode(message, "UTF-8");
                    httpClientService.get(url);
                }
            }
        }

        // Save historic
        User user = userRepo.findByTri(tri);
        Historic historic = new Historic(endDate, user);
        historicRepo.save(historic);
        return Response.ok().build();
    }

    @NotNull
    private static String getString(Kpi kpi) {
        final long a = kpi.getCb_30j();
        final long b = kpi.getParc();
        final double c = ((double) a / b) * 100;
        String message = "Données du " + kpi.getJour() +
                " : zone (" + kpi.getZone() + "), parc (" + kpi.getParc() + "), delta parc (" + kpi.getDelta_parc() + "), " +
                "charged base (" + kpi.getCb_30j() + "), taux charged base (" + String.format("%.2f", c) + "), " +
                "act (" + kpi.getActivation() + "), cum act (" + kpi.getCumul_activation() + "), " +
                "mtt rec (" + (int) kpi.getMtt_rec().doubleValue() + "), " +
                "mtt cum rec (" + (int) kpi.getCumul_mtt_rec().doubleValue() + ")";
        return message;
    }

    public Response getZone() {
        List<Zone> zones = dwhRepo.getAllZone();
        return Response.ok(zones).build();
    }

    public Response getHistoric(String date) {
        List<Historic> historics = historicRepo.getAll(LocalDate.parse(date));
        return Response.ok(historics).build();
    }
}
