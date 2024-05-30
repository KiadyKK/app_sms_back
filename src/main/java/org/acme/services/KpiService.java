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
    private static final Logger logger = Logger.getLogger(KpiService.class);
    public Response getDwh() {
        try{
            LocalDate yesterday=LocalDate.now().minusDays(1);
            LocalDate startDate=yesterday.withDayOfMonth(1);
            LocalDate endDate=yesterday;
            List<DwhRes> dwhResList = dwhRepo.getAll(startDate, endDate);
            return Response.ok(dwhResList).build();
        }catch(Exception e){
            return Response.serverError().build();
        }
    }

    public Response testSms(String msisdn){
        try{
            String url = "http://10.249.248.40:80/cgi-bin/sendsms?username=smsgw&password=mypass&from=Sms833&to=" + msisdn + "&text=test+app+sms+833";
            String res = httpClientService.get(url);
            System.out.println("=====================>" + res);
            return Response.noContent().build();
        }catch (Exception e){
            return Response.serverError().build();
        }
    }

    public Response getAll(String date) {
        try{
            List<Kpi> kpis = kpiRepo.getAll(LocalDate.parse(date));
            return Response.ok(kpis).build();
        }catch (Exception e){
            return Response.serverError().build();
        }
    }

    public Response getAllDwh(String date) {
        LocalDate day=LocalDate.parse(date);
        LocalDate startDate=day.withDayOfMonth(1);
        LocalDate endDate=day;
        try{
            List<DwhRes> dwhResList = dwhRepo.getAll(startDate, endDate);
            Map<LocalDate, List<DwhRes>> dwhResListGrouped = dwhResList.stream().collect(Collectors.groupingBy(DwhRes::getJour));
            boolean check = false;
            for (LocalDate jour : dwhResListGrouped.keySet()) {
                check = dwhResListGrouped.get(jour).stream().allMatch(dwhRes -> dwhRes.getParc() == 0);
                if (check) break;
            }
            if (!check) {
                //Remove all data before persist
                kpiRepo.removeAll(LocalDate.parse(date));

                for (DwhRes dwhRes : dwhResList) {
                    Kpi kpi = new Kpi(dwhRes);
                    kpiRepo.save(kpi);
                }
            } else {
                dwhResList = new ArrayList<>();
            }
            return Response.ok(dwhResList).build();
        }catch (Exception e){
            return Response.serverError().build();
        }

    }
    public Response saveHistoric(LocalDate endDate,User user){
        try{
            Historic historic=new Historic(endDate,user);
            historicRepo.save(historic);
            return Response.ok().build();
        }catch (Exception e){
            return Response.serverError().build();
        }
    }
    public Response sendSms(String date, String tri) throws UnsupportedEncodingException {
        LocalDate yesterday=LocalDate.now().minusDays(1);
        LocalDate startDate=yesterday.withDayOfMonth(1);
        LocalDate endDate=yesterday;
        try{
            logger.info("Fetching RDZs...");
            List<Rdz> rdzs = rdzRepo.getAll("");
            logger.info("Rdz fetched "+ rdzs.size());

            logger.info("Fetching KPIs...");
            List<Kpi> kpis = kpiRepo.getAll(LocalDate.parse(date));
            logger.info("KPIs fetched..."+kpis.size());
            for (Kpi kpi : kpis) {
                String message = "Données du " + kpi.getJour() +
                        " : zone (" + kpi.getZone() + "), parc (" + kpi.getParc() + "), " +
                        "charged base (" + kpi.getCb_30j() + "), taux charged base (" + String.format("%.2f", (kpi.getCb_30j() / (double) kpi.getParc()) * 100) +
                        "%), act (" + kpi.getActivation() + "), cum act (" + kpi.getCumul_activation() + "), " +
                        "mtt rec (" + String.format("%.2f", kpi.getMtt_rec()) + "), " +
                        "mtt cum rec (" + String.format("%.2f", kpi.getCumul_mtt_rec()) + ")";
                for (Rdz rdz : rdzs) {
                    if (kpi.getZone().equals(rdz.getZone())) {
                        String url = "http://10.249.248.40:80/cgi-bin/sendsms?username=smsgw&password=mypass&from=" + APP_NAME + "&to=" + rdz.getTel() + "&text=" + URLEncoder.encode(message, "UTF-8");
                        logger.info("Sending SMS to URL: " + url);
                        httpClientService.get(url);
                    }
                }
            }
            User user = userRepo.findByTri(tri);
            logger.info("User fetched: " + user.getTri());
            return saveHistoric(endDate,user);
        }catch (Exception e){
            logger.error("Error occurred in sendSms: ");
            return Response.serverError().build();
        }
    }

    public Response getZone() {
        try{
            List<Zone> zones = dwhRepo.getAllZone();
            return Response.ok(zones).build();
        }catch(Exception e){
            return Response.serverError().build();
        }
    }
    public Response getHistoric(String date) {
        try{
            List<Historic> historics = historicRepo.getAll(LocalDate.parse(date));
            return Response.ok(historics).build();
        }catch(Exception e){
            return Response.serverError().build();
        }
    }
}
