package org.acme.services;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.middleware.HttpClientService;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@QuarkusTest
public class KpiServiceTest {
    private static final Logger log = LoggerFactory.getLogger(KpiServiceTest.class);
    @InjectMock
    KpiRepo kpiRepo;
    @InjectMock
    RdzRepo rdzRepo;
    @InjectMock
    DwhRepo dwhRepo;
    @InjectMock
    HistoricRepo historicRepo;
    @InjectMock
    HttpClientService httpClientService;
    @InjectMock
    UserRepo userRepo;
    @Inject
    KpiService kpiService;
    private static List<Zone> zones;
    private static List<Historic> historics;
    private static LocalDate date;
    private static LocalDate sendDate;
    private static List<DwhRes> dwhResList;
    private static List<Kpi> kpis;
    private static List<Rdz> rdzs;
    private static User user;
    private static Historic historic;
    @BeforeEach
    void setup(){
        Zone zone=Mockito.mock(Zone.class);
        Mockito.when(zone.getName()).thenReturn("Alaotra");
        Zone zone1=Mockito.mock(Zone.class);
        Mockito.when(zone1.getName()).thenReturn("Itasy");
        zones=Arrays.asList(zone,zone1);

        date=LocalDate.parse("2018-08-01");
        sendDate=LocalDate.parse("2018-08-24");

        historic=Mockito.mock(Historic.class);
        Mockito.when(historic.getId()).thenReturn(1L);
        Mockito.when(historic.getIdUser()).thenReturn(1L);
        Mockito.when(historic.getKpiDate()).thenReturn(date);
        Mockito.when(historic.getSendDate()).thenReturn(sendDate);
        Mockito.when(historic.getTriUser()).thenReturn("iol");

        historics=Arrays.asList(historic);

        DwhRes dwhRes1=Mockito.mock(DwhRes.class);
        DwhRes dwhRes2=Mockito.mock(DwhRes.class);
        Mockito.when(dwhRes1.getZone()).thenReturn("Alaotra");
        Mockito.when(dwhRes1.getActivation()).thenReturn(1L);
        Mockito.when(dwhRes1.getJour()).thenReturn(LocalDate.parse("2024-05-25"));
        Mockito.when(dwhRes1.getMois_annee()).thenReturn("05-25");
        Mockito.when(dwhRes1.getParc()).thenReturn(2L);
        Mockito.when(dwhRes1.getMtt_rec()).thenReturn(3.7);
        Mockito.when(dwhRes1.getCumul_mtt_rec()).thenReturn(56.9);
        Mockito.when(dwhRes1.getCumul_activation()).thenReturn(5L);
        Mockito.when(dwhRes1.getCb_7j()).thenReturn(5L);
        Mockito.when(dwhRes1.getCb_30j()).thenReturn(6L);
        Mockito.when(dwhRes1.getCb_30jd()).thenReturn(7L);

        Mockito.when(dwhRes2.getZone()).thenReturn("Itasy");
        Mockito.when(dwhRes2.getActivation()).thenReturn(2L);
        Mockito.when(dwhRes2.getJour()).thenReturn(LocalDate.parse("2024-05-25"));
        Mockito.when(dwhRes2.getMois_annee()).thenReturn("05-25");
        Mockito.when(dwhRes2.getParc()).thenReturn(98L);
        Mockito.when(dwhRes2.getMtt_rec()).thenReturn(9.7);
        Mockito.when(dwhRes2.getCumul_mtt_rec()).thenReturn(73.9);
        Mockito.when(dwhRes2.getCumul_activation()).thenReturn(6L);
        Mockito.when(dwhRes2.getCb_7j()).thenReturn(9L);
        Mockito.when(dwhRes2.getCb_30j()).thenReturn(3L);
        Mockito.when(dwhRes2.getCb_30jd()).thenReturn(2L);

        dwhResList= Arrays.asList(dwhRes1,dwhRes2);

        Kpi kpi=Mockito.mock(Kpi.class);
        Mockito.when(kpi.getZone()).thenReturn("Alaotra");
        Mockito.when(kpi.getActivation()).thenReturn(2L);
        Mockito.when(kpi.getJour()).thenReturn(LocalDate.parse("2018-08-04"));
        Mockito.when(kpi.getMois_annee()).thenReturn("08-04");
        Mockito.when(kpi.getParc()).thenReturn(98L);
        Mockito.when(kpi.getMtt_rec()).thenReturn(9.7);
        Mockito.when(kpi.getCumul_mtt_rec()).thenReturn(73.9);
        Mockito.when(kpi.getCumul_activation()).thenReturn(6L);
        Mockito.when(kpi.getCb_7j()).thenReturn(9L);
        Mockito.when(kpi.getCb_30j()).thenReturn(3L);
        Mockito.when(kpi.getCb_30jd()).thenReturn(2L);

        Kpi kpi1=Mockito.mock(Kpi.class);
        Mockito.when(kpi1.getZone()).thenReturn("Itasy");
        Mockito.when(kpi1.getActivation()).thenReturn(2L);
        Mockito.when(kpi1.getJour()).thenReturn(LocalDate.parse("2018-08-04"));
        Mockito.when(kpi1.getMois_annee()).thenReturn("08-04");
        Mockito.when(kpi1.getParc()).thenReturn(98L);
        Mockito.when(kpi1.getMtt_rec()).thenReturn(9.7);
        Mockito.when(kpi1.getCumul_mtt_rec()).thenReturn(73.9);
        Mockito.when(kpi1.getCumul_activation()).thenReturn(6L);
        Mockito.when(kpi1.getCb_7j()).thenReturn(9L);
        Mockito.when(kpi1.getCb_30j()).thenReturn(3L);
        Mockito.when(kpi1.getCb_30jd()).thenReturn(2L);
        kpis=Arrays.asList(kpi,kpi1);

        Rdz rdz=Mockito.mock(Rdz.class);
        Mockito.when(rdz.getZone()).thenReturn("Alaotra");
        Mockito.when(rdz.getTel()).thenReturn("0329966415");
        Mockito.when(rdz.getTri()).thenReturn("iol");
        Mockito.when(rdz.getNom()).thenReturn("Rakoto");
        Mockito.when(rdz.getPrenom()).thenReturn("Michelle");
        Mockito.when(rdz.getEmail()).thenReturn("mi@gmail.com");

        Rdz rdz1=Mockito.mock(Rdz.class);
        Mockito.when(rdz1.getZone()).thenReturn("Itasy");
        Mockito.when(rdz1.getTel()).thenReturn("0329966416");
        Mockito.when(rdz1.getTri()).thenReturn("dny");
        Mockito.when(rdz1.getNom()).thenReturn("Rakotovao");
        Mockito.when(rdz1.getPrenom()).thenReturn("Andri");
        Mockito.when(rdz1.getEmail()).thenReturn("An@gmail.com");
        rdzs=Arrays.asList(rdz,rdz1);

        user=Mockito.mock(User.class);
        Mockito.when(userRepo.findByTri(anyString())).thenReturn(user);
    }
    @Test
    void getALlZoneTest(){
        Mockito.when(dwhRepo.getAllZone()).thenReturn(zones);
        Response response=kpiService.getZone();
        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
        List<Zone> entity=(List<Zone>) response.getEntity();
        assertEquals(2,entity.size());
        assertEquals(zones.get(0).getName(),entity.get(0).getName());
        assertEquals(zones.get(1).getName(),entity.get(1).getName());

        Mockito.verify(dwhRepo, times(1)).getAllZone();
    }
    @Test
    void getAllZoneWithException(){
        Mockito.when(dwhRepo.getAllZone()).thenThrow(new RuntimeException("Database error"));
        Response response=kpiService.getZone();

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());

        Mockito.verify(dwhRepo).getAllZone();
    }
    @Test
    void getHistoricTest(){
        String dateExemple="2018-08-01";
        Mockito.when(historicRepo.getAll(date)).thenReturn(historics);
        Response response=kpiService.getHistoric(dateExemple);

        assertNotNull(response);
        assertNotNull(response.getEntity());
        List<Historic> entity=(List<Historic>) response.getEntity();
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
        assertEquals(1,entity.size());
        assertEquals(historics.get(0).getTriUser(),entity.get(0).getTriUser());
        assertEquals(historics.get(0).getKpiDate(),entity.get(0).getKpiDate());
        assertEquals(historics.get(0).getSendDate(),entity.get(0).getSendDate());
        assertEquals(historics.get(0).getIdUser(),entity.get(0).getIdUser());

        Mockito.verify(historicRepo, times(1)).getAll(any(LocalDate.class));
    }
    @Test
    void getHistoricWithException(){
        String date="2018-08-01";
        Mockito.when(historicRepo.getAll(LocalDate.parse(date))).thenThrow(new RuntimeException("Database error"));

        Response response =kpiService.getHistoric(date);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());

        Mockito.verify(historicRepo).getAll(any(LocalDate.class));

    }
    @Test
    void getDwhTest(){
        LocalDate yesterday= LocalDate.now().minusDays(1);
        LocalDate startDate=yesterday.withDayOfMonth(1);
        Mockito.when(dwhRepo.getAll(startDate,yesterday)).thenReturn(dwhResList);

        Response response=kpiService.getDwh();

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());

        List<DwhRes> entity=(List<DwhRes>) response.getEntity();
        assertEquals(2,entity.size());
        assertEquals(dwhResList.get(0).getParc(),entity.get(0).getParc());
        assertEquals(dwhResList.get(1).getJour(),entity.get(1).getJour());

        Mockito.verify(dwhRepo).getAll(any(LocalDate.class),any(LocalDate.class));
    }
    @Test
    void shouldHandleExceptionGracefully() {

        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate startDate = yesterday.withDayOfMonth(1);

        Mockito.when(dwhRepo.getAll(startDate, yesterday)).thenThrow(new RuntimeException("Database error"));

        Response response = kpiService.getDwh();

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        Mockito.verify(dwhRepo, times(1)).getAll(any(LocalDate.class),any(LocalDate.class));
    }
    @Test
    void testSmsSuccess() throws Exception{
        String msisdn="0123456789";
        String excpectedResponse="I am an excpected response";
        Mockito.when(httpClientService.get(anyString())).thenReturn(excpectedResponse);
        Response response=kpiService.testSms(msisdn);
        Mockito.verify(httpClientService,times(1)).get(anyString());
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(),response.getStatus());
    }
    @Test
    void testSmsError() throws Exception{
        String msisdn="0123456789";
        Mockito.when(httpClientService.get(anyString())).thenThrow(new RuntimeException("Failed to get Url"));
        Response response =kpiService.testSms(msisdn);
        Mockito.verify(httpClientService, times(1)).get(anyString());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void sendSmsTest() throws UnsupportedEncodingException {
        String excpectedResponse="I am an excpected response";
        Mockito.when(rdzRepo.getAll(anyString())).thenReturn(rdzs);
        Mockito.when(kpiRepo.getAll(LocalDate.parse("2018-08-01"))).thenReturn(kpis);
        Mockito.when(userRepo.findByTri(anyString())).thenReturn(user);
        Mockito.when(httpClientService.get(anyString())).thenReturn(excpectedResponse);
        Response response=kpiService.sendSms("2018-08-01","iol");
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());

        Mockito.verify(rdzRepo).getAll(anyString());
        Mockito.verify(kpiRepo).getAll(any(LocalDate.class));
        Mockito.verify(userRepo).findByTri(anyString());
        Mockito.verify(httpClientService,times(2)).get(anyString());
    }
    @Test
    void sendSmsTestException() throws Exception {
        Mockito.when(rdzRepo.getAll(anyString())).thenReturn(rdzs);
        Mockito.when(kpiRepo.getAll(LocalDate.parse("2018-08-01"))).thenReturn(kpis);
        Mockito.when(userRepo.findByTri(anyString())).thenReturn(user);
        Mockito.when(httpClientService.get(anyString())).thenThrow(new RuntimeException());

        Response response=kpiService.sendSms("2018-08-01","iol");
        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }
    @Test
    void saveHistoric(){
        doNothing().when(historicRepo).save(any(Historic.class));
        Response response=kpiService.saveHistoric(LocalDate.now(),user);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
        Mockito.verify(historicRepo).save(any(Historic.class));
    }
    @Test
    void saveHistoricWithException(){
        doThrow(new RuntimeException()).when(historicRepo).save(any(Historic.class));

        Response response=kpiService.saveHistoric(LocalDate.now(),user);
        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());

        Mockito.verify(historicRepo).save(any(Historic.class));
    }
    @Test
    void getAllDwhWithOkResponse(){
        LocalDate date=LocalDate.of(2024,05,25);
        LocalDate startDate=date.withDayOfMonth(1);
        LocalDate endDate=date;

        Mockito.when(dwhRepo.getAll(startDate,endDate)).thenReturn(dwhResList);
        Mockito.when(kpiRepo.removeAll(any(LocalDate.class))).thenReturn(6L);
        doNothing().when(kpiRepo).save(any(Kpi.class));

        Response response=kpiService.getAllDwh(date.toString());
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<DwhRes> returnedDwhResList = (List<DwhRes>) response.getEntity();
        assertEquals(2, returnedDwhResList.size());
        Mockito.verify(dwhRepo).getAll(startDate, endDate);
        Mockito.verify(kpiRepo).removeAll(date);
        Mockito.verify(kpiRepo, times(2)).save(any(Kpi.class));
    }
    @Test
    void getAllDwhWithEmptyList(){
        LocalDate date=LocalDate.of(2024,05,25);
        LocalDate startDate=date.withDayOfMonth(1);
        LocalDate endDate=date;

        DwhRes dwhRes=Mockito.mock(DwhRes.class);
        DwhRes dwhRes3=Mockito.mock(DwhRes.class);
        Mockito.when(dwhRes.getZone()).thenReturn("Alaotra");
        Mockito.when(dwhRes.getActivation()).thenReturn(1L);
        Mockito.when(dwhRes.getJour()).thenReturn(LocalDate.parse("2024-05-25"));
        Mockito.when(dwhRes.getMois_annee()).thenReturn("05-25");
        Mockito.when(dwhRes.getParc()).thenReturn(0L);
        Mockito.when(dwhRes.getMtt_rec()).thenReturn(3.7);
        Mockito.when(dwhRes.getCumul_mtt_rec()).thenReturn(56.9);
        Mockito.when(dwhRes.getCumul_activation()).thenReturn(5L);
        Mockito.when(dwhRes.getCb_7j()).thenReturn(5L);
        Mockito.when(dwhRes.getCb_30j()).thenReturn(6L);
        Mockito.when(dwhRes.getCb_30jd()).thenReturn(7L);

        Mockito.when(dwhRes3.getZone()).thenReturn("Itasy");
        Mockito.when(dwhRes3.getActivation()).thenReturn(1L);
        Mockito.when(dwhRes3.getJour()).thenReturn(LocalDate.parse("2024-05-25"));
        Mockito.when(dwhRes3.getMois_annee()).thenReturn("05-25");
        Mockito.when(dwhRes3.getParc()).thenReturn(0L);
        Mockito.when(dwhRes3.getMtt_rec()).thenReturn(3.9);
        Mockito.when(dwhRes3.getCumul_mtt_rec()).thenReturn(6.9);
        Mockito.when(dwhRes3.getCumul_activation()).thenReturn(9L);
        Mockito.when(dwhRes3.getCb_7j()).thenReturn(6L);
        Mockito.when(dwhRes3.getCb_30j()).thenReturn(4L);
        Mockito.when(dwhRes3.getCb_30jd()).thenReturn(3L);

        List<DwhRes> list=new ArrayList<>();
        list=Arrays.asList(dwhRes,dwhRes3);

        Mockito.when(dwhRepo.getAll(startDate,endDate)).thenReturn(list);
        Response response = kpiService.getAllDwh(date.toString());

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<DwhRes> returnedDwhResList = (List<DwhRes>) response.getEntity();
        assertTrue(returnedDwhResList.isEmpty());
        verify(dwhRepo).getAll(startDate, endDate);
        verify(kpiRepo, never()).removeAll(any(LocalDate.class));
        verify(kpiRepo, never()).save(any(Kpi.class));
    }
    @Test
    void getAllDwhShouldReturnServerError(){
        LocalDate date= LocalDate.of(2024,05,25);
        LocalDate startDate=date.withDayOfMonth(1);
        LocalDate endDate=date;
        Mockito.when(dwhRepo.getAll(startDate,endDate)).thenThrow(new RuntimeException("Database error"));

        Response response=kpiService.getAllDwh(date.toString());

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
        Mockito.verify(dwhRepo).getAll(any(LocalDate.class),any(LocalDate.class));
        verify(kpiRepo, never()).removeAll(any(LocalDate.class));
        verify(kpiRepo, never()).save(any(Kpi.class));
    }
    @Test
    void getAllTestShouldReturnOk(){
        LocalDate date=LocalDate.of(2024,05,25);
        Mockito.when(kpiRepo.getAll(date)).thenReturn(kpis);

        Response response=kpiService.getAll(date.toString());
        List<Kpi> entity=(List<Kpi>) response.getEntity();

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(kpis, response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
        assertEquals(2,entity.size());

        Mockito.verify(kpiRepo).getAll(any(LocalDate.class));
    }
    @Test
    void getAllShouldReturnServerError(){
        LocalDate date=LocalDate.of(2024,05,25);
        Mockito.when(kpiRepo.getAll(date)).thenThrow(new RuntimeException("Database Error"));

        Response response=kpiService.getAll(date.toString());

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
        Mockito.verify(kpiRepo).getAll(any(LocalDate.class));

    }
}

