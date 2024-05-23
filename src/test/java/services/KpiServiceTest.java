package services;
import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.model.app_sms_833.Historic;
import org.acme.model.dm_rf.DwhRes;
import org.acme.model.dm_rf.Zone;
import org.acme.repo.app_sms_833.HistoricRepo;
import org.acme.repo.app_sms_833.KpiRepo;
import org.acme.repo.dm_rf.DwhRepo;
import org.acme.services.KpiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@QuarkusTest
public class KpiServiceTest {
    @InjectMock
    KpiRepo kpiRepo;
    @InjectMock
    DwhRepo dwhRepo;
    @InjectMock
    HistoricRepo historicRepo;
    @Inject
    KpiService kpiService;
    private List<Zone> zones;
    private List<Historic> historics;
    private LocalDate date;
    private LocalDate sendDate;
    private List<DwhRes> dwhResList;
    @BeforeEach
    void setup(){
        Zone zone=Mockito.mock(Zone.class);
        Mockito.when(zone.getName()).thenReturn("Alaotra");
        Zone zone1=Mockito.mock(Zone.class);
        Mockito.when(zone1.getName()).thenReturn("Itasy");
        zones=Arrays.asList(zone,zone1);

        date=LocalDate.parse("2018-08-01");
        sendDate=LocalDate.parse("2018-08-24");

        historics=new ArrayList<>();
        Historic historic=Mockito.mock(Historic.class);
        Mockito.when(historic.getId()).thenReturn(1L);
        Mockito.when(historic.getIdUser()).thenReturn(1L);
        Mockito.when(historic.getKpiDate()).thenReturn(date);
        Mockito.when(historic.getSendDate()).thenReturn(sendDate);
        Mockito.when(historic.getTriUser()).thenReturn("iol");

        historics.add(historic);

        DwhRes dwhRes1=Mockito.mock(DwhRes.class);
        DwhRes dwhRes2=Mockito.mock(DwhRes.class);
        Mockito.when(dwhRes1.getZone()).thenReturn("Alaotra");
        Mockito.when(dwhRes1.getActivation()).thenReturn(1L);
        Mockito.when(dwhRes1.getJour()).thenReturn(LocalDate.parse("2018-08-01"));
        Mockito.when(dwhRes1.getMois_annee()).thenReturn("08-01");
        Mockito.when(dwhRes1.getParc()).thenReturn(2L);
        Mockito.when(dwhRes1.getMtt_rec()).thenReturn(3.7);
        Mockito.when(dwhRes1.getCumul_mtt_rec()).thenReturn(56.9);
        Mockito.when(dwhRes1.getCumul_activation()).thenReturn(5L);
        Mockito.when(dwhRes1.getCb_7j()).thenReturn(5L);
        Mockito.when(dwhRes1.getCb_30j()).thenReturn(6L);
        Mockito.when(dwhRes1.getCb_30jd()).thenReturn(7L);


        Mockito.when(dwhRes2.getZone()).thenReturn("Itasy");
        Mockito.when(dwhRes2.getActivation()).thenReturn(2L);
        Mockito.when(dwhRes2.getJour()).thenReturn(LocalDate.parse("2018-08-04"));
        Mockito.when(dwhRes2.getMois_annee()).thenReturn("08-04");
        Mockito.when(dwhRes2.getParc()).thenReturn(98L);
        Mockito.when(dwhRes2.getMtt_rec()).thenReturn(9.7);
        Mockito.when(dwhRes2.getCumul_mtt_rec()).thenReturn(73.9);
        Mockito.when(dwhRes2.getCumul_activation()).thenReturn(6L);
        Mockito.when(dwhRes2.getCb_7j()).thenReturn(9L);
        Mockito.when(dwhRes2.getCb_30j()).thenReturn(3L);
        Mockito.when(dwhRes2.getCb_30jd()).thenReturn(2L);

        dwhResList= Arrays.asList(dwhRes1,dwhRes2);

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

        Mockito.verify(dwhRepo,Mockito.times(1)).getAllZone();
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

        Mockito.verify(historicRepo,Mockito.times(1)).getAll(date);

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

        Mockito.verify(dwhRepo).getAll(startDate,yesterday);
    }
    @Test
    void shouldHandleExceptionGracefully() {

        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate startDate = yesterday.withDayOfMonth(1);

        Mockito.when(dwhRepo.getAll(startDate, yesterday)).thenThrow(new RuntimeException("Database error"));

        Response response = kpiService.getDwh();

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        Mockito.verify(dwhRepo, Mockito.times(1)).getAll(startDate, yesterday);
    }
}
