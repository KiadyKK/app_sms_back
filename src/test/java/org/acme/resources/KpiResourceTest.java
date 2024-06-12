package org.acme.resources;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestExtension;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.model.dm_rf.DwhRes;
import org.acme.model.dm_rf.Zone;
import org.acme.services.KpiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.acme.common.Constant.APPSMS;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
@QuarkusTest
class KpiResourceTest extends QuarkusTestExtension {
    @InjectMock
    KpiService kpiService;
    @Inject
    SecurityContext securityContext;
    @TestHTTPResource("/kpi/zone")
    URL kpiZoneUrl;
    @BeforeEach
    void setUp() {
    }
    @Test
    void getDwhSuccess() {
        DwhRes dwhres= new DwhRes();
        dwhres.setActivation(4L);
        dwhres.setCb_7j(8L);
        dwhres.setCb_30j(9L);
        dwhres.setCb_30jd(10L);
        dwhres.setCumul_activation(20L);
        dwhres.setCumul_mtt_rec(78.9);
        dwhres.setJour(LocalDate.parse("2018-08-01"));
        dwhres.setMois_annee("08-01");
        dwhres.setMtt_rec(67.9);
        dwhres.setParc(8L);
        dwhres.setZone("Alaotra");

        DwhRes dwhRes1=new DwhRes();
        dwhRes1.setActivation(4L);
        dwhRes1.setCb_7j(8l);
        dwhRes1.setCb_30j(9L);
        dwhRes1.setCb_30jd(10L);
        dwhRes1.setCumul_activation(20L);
        dwhRes1.setCumul_mtt_rec(78.9);
        dwhRes1.setJour(LocalDate.parse("2018-08-01"));
        dwhRes1.setMois_annee("08-01");
        dwhRes1.setMtt_rec(67.9);
        dwhRes1.setParc(8L);
        dwhRes1.setZone("Itasy");
        List<DwhRes> list= Arrays.asList(dwhres,dwhRes1);
        Mockito.when(kpiService.getDwh()).thenReturn(Response.ok(list).build());
        given()
                .when().get("/kpi/cron")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body("[0].jour", is(dwhres.getJour().toString()))
                .body("[0].zone", is(dwhres.getZone()))
                .body("[0].cumul_mtt_rec", is(dwhres.getCumul_mtt_rec().floatValue()))
                .body("[0].mois_annee", is(dwhres.getMois_annee()))
                .body("[0].mtt_rec", is(dwhres.getMtt_rec().floatValue()))
                .body("[1].jour", is(dwhRes1.getJour().toString()))
                .body("[1].zone", is(dwhRes1.getZone()))
                .body("[1].cumul_mtt_rec", is(dwhRes1.getCumul_mtt_rec().floatValue()))
                .body("[1].mois_annee", is(dwhRes1.getMois_annee()))
                .body("[1].mtt_rec", is(dwhRes1.getMtt_rec().floatValue()));
        Mockito.verify(kpiService).getDwh();
    }
    @Test
    void getDwhException(){
        DwhRes dwhRes=new DwhRes();
        DwhRes dwhRes1=new DwhRes();
        List<DwhRes>list=Arrays.asList(dwhRes,dwhRes1);
        Mockito.when(kpiService.getDwh()).thenThrow(new RuntimeException("Database error"));
        given()
                .when().get("/kpi/cron")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON);
        Mockito.verify(kpiService).getDwh();
    }
    @Test
    void testJson() {
        String msisdn="0323232323";
        Mockito.when(kpiService.testSms(any(String.class))).thenReturn(Response.noContent().build());
        given()
                .pathParam("msisdn",msisdn)
                .when().get("/kpi/test-sms/{msisdn}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        Mockito.verify(kpiService).testSms(any(String.class));
    }
    @Test
    void testJsonException(){
        String msisdn="0323232323";
        Mockito.when(kpiService.testSms(any(String.class))).thenThrow(new RuntimeException("error occured"));
        given()
                .pathParam("msisdn",msisdn)
                .when().get("/kpi/test-sms/{msisdn}")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.verify(kpiService).testSms(any(String.class));
    }
    @Test
    void getAll() {

    }

    @Test
    void getAllDwhSuccess() {
        String date="2018-08-01";
        DwhRes dwhres=new DwhRes();
        dwhres.setActivation(4L);
        dwhres.setCb_7j(8L);
        dwhres.setCb_30j(9L);
        dwhres.setCb_30jd(10L);
        dwhres.setCumul_activation(20L);
        dwhres.setCumul_mtt_rec(78.9);
        dwhres.setJour(LocalDate.parse("2018-08-01"));
        dwhres.setMois_annee("08-01");
        dwhres.setMtt_rec(67.9);
        dwhres.setParc(8L);
        dwhres.setZone("Alaotra");

        DwhRes dwhRes1=new DwhRes();
        dwhRes1.setActivation(4L);
        dwhRes1.setCb_7j(8l);
        dwhRes1.setCb_30j(9L);
        dwhRes1.setCb_30jd(10L);
        dwhRes1.setCumul_activation(20L);
        dwhRes1.setCumul_mtt_rec(78.9);
        dwhRes1.setJour(LocalDate.parse("2018-08-01"));
        dwhRes1.setMois_annee("08-01");
        dwhRes1.setMtt_rec(67.9);
        dwhRes1.setParc(8L);
        dwhRes1.setZone("Itasy");

        List<DwhRes>list=Arrays.asList(dwhres,dwhRes1);
        Mockito.when(kpiService.getAllDwh(date)).thenReturn(Response.ok(list).build());
        given()
                .queryParam("date",date)
                .when().get("/kpi/dwh")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body("[0].jour",is(dwhres.getJour().toString()))
                .body("[1].mtt_rec",is(dwhRes1.getMtt_rec().floatValue()));
        Mockito.verify(kpiService).getAllDwh(any(String.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void getZoneSuccess() {
        Zone zone=new Zone();
        zone.setName("Alaotra");

        Zone zone1=new Zone();
        zone1.setName("Itasy");
        List<Zone> listZone=new ArrayList<>();
        listZone.add(zone);
        listZone.add(zone1);
        Mockito.when(kpiService.getZone()).thenReturn(Response.ok(listZone).type(MediaType.APPLICATION_JSON).build());

        given()
                .when().get(kpiZoneUrl)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("[0].name",is(zone.getName()))
                .body("[1].name",is(zone1.getName()));
        Mockito.verify(kpiService).getZone();

    }

    @Test
    void send() {
    }

    @Test
    void getHistoric() {
    }
}