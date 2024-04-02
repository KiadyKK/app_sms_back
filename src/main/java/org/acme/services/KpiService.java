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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

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

    public Response getDwh() {
//        LocalDate startDate = LocalDate.now().minusDays(1);
//        LocalDate endDate = startDate.plusDays(1);
//        String checkData;
//
//        Optional<Kpi> optional = Optional.ofNullable(kpiRepo.getByJour(startDate));
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
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = startDate.plusDays(1);
        List<DwhRes> dwhResList = dwhRepo.getAll(startDate, endDate);
        return Response.ok(dwhResList).build();
    }

    public Response testSms(String msisdn) {
        String url = "http://10.249.248.40:80/cgi-bin/sendsms?username=smsgw&password=mypass&from=Sms833&to=" + msisdn + "&text=test+app+sms+833";
        String res = httpClientService.get(url);
        System.out.println("=====================>" + res);

//        HttpClient client = HttpClientBuilder.create().build();
//        HttpGet request = new HttpGet(url);
//        HttpResponse response = client.execute(request);
//        String responseBody = EntityUtils.toString(response.getEntity());
//        System.out.println("=====================>" + responseBody);

        return Response.noContent().build();
    }

    public Response getAll(String date) {
        List<Kpi> kpis = kpiRepo.getAll(LocalDate.parse(date));
        return Response.ok(kpis).build();
    }

    public Response getAllDwh(String date) {
        //Remove all data before persist
        kpiRepo.removeAll(LocalDate.parse(date));

        LocalDate startDate = LocalDate.parse(date).minusDays(1);
        LocalDate endDate = startDate.plusDays(1);
        List<DwhRes> dwhResList = dwhRepo.getAll(startDate, endDate);
        if (!dwhResList.isEmpty()) {
            for (DwhRes dwhRes : dwhResList) {
                Kpi kpi = new Kpi(dwhRes);
                kpiRepo.save(kpi);
            }
        }
        return Response.ok(dwhResList).build();
    }

    public Response sendSms(String date, String source, String tri) throws UnsupportedEncodingException {
        LocalDate startDate = LocalDate.parse(date).minusDays(1);
        LocalDate endDate = startDate.plusDays(1);
        List<Rdz> rdzs = rdzRepo.getAll("");
//        if (source.equals("app")) {
        List<Kpi> kpis = kpiRepo.getAll(LocalDate.parse(date));
        for (Kpi kpi : kpis) {
            String message = "Données du " + kpi.getJour() +
                    " : zone (" + kpi.getZone() + "), parc (" + kpi.getParc() + "), " +
                    "cb 30jours (" + kpi.getCb_30jours() +"), cb 7jours (" + kpi.getCb_7jours() + "), cb 30jours data (" + kpi.getCb_30jours_data() + "), " +
                    "act (" + kpi.getActivation() + "), cum act (" + kpi.getCumul_activation() + "), " +
                    "mtt rec (" + String.format("%.2f", kpi.getMtt_rec()) + "), " +
                    "mtt cum rec (" + String.format("%.2f", kpi.getCumul_mtt_rec()) + ")";
            for (Rdz rdz : rdzs) {
                if (kpi.getZone().equals(rdz.getZone())) {
                    String url = "http://10.249.248.40:80/cgi-bin/sendsms?username=smsgw&password=mypass&from=" + APP_NAME + "&to=" + rdz.getTel() + "&text=" + URLEncoder.encode(message, "UTF-8");
                    httpClientService.get(url);
                }
            }
        }
//        } else {
//            List<DwhRes> dwhResList = dwhRepo.getAll(startDate, endDate);
//            for (DwhRes dwhRes : dwhResList) {
//                String message = "Données du " + dwhRes.getJour() +
//                        " : zone (" + dwhRes.getZone() + "), parc (" + dwhRes.getParc() + "), " +
//                        "act (" + dwhRes.getActivation() + "), cum act (" + dwhRes.getCumul_activation() + "), " +
//                        "rec (" + dwhRes.getNb_rec() + "), mtt rec (" + dwhRes.getMtt_rec() + "), " +
//                        "cum rec (" + dwhRes.getCumul_nb_rec() + "), mtt cum rec (" + dwhRes.getCumul_mtt_rec() + ")";
//                for (Rdz rdz : rdzs) {
//                    if (dwhRes.getZone().equals(rdz.getZone())) {
//                        String url = "http://10.249.248.40:80/cgi-bin/smssend?username=smsgw&password=mypass&from=" + APP_NAME + "&to=" + rdz.getTel() + "&text=" + URLEncoder.encode(message, "UTF-8");
//                        httpClientService.get(url);
//                    }
//                }
//            }
//        }

        // Save historic
        User user = userRepo.findByTri(tri);
        Historic historic = new Historic(endDate, user);
        historicRepo.save(historic);
        return Response.ok().build();
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
