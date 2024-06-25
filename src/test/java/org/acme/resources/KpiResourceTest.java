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
import org.acme.model.app_sms_833.Historic;
import org.acme.model.app_sms_833.Kpi;
import org.acme.model.app_sms_833.User;
import org.acme.model.dm_rf.DwhRes;
import org.acme.model.dm_rf.Zone;
import org.acme.services.KpiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.UnsupportedEncodingException;
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
    void testJson() throws Exception {
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
    void testJsonException() throws Exception{
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
    @TestSecurity(authorizationEnabled = false)
    void getAllSuccess() {
        String date="2018-08-01";
        Kpi kpi=new Kpi();
        kpi.setMtt_rec(7.9);
        kpi.setActivation(7);
        kpi.setCb_7j(8);
        kpi.setCb_30j(7);
        kpi.setCb_30jd(5);
        kpi.setCumul_activation(89);
        kpi.setCumul_mtt_rec(78.4);
        kpi.setCumul_mtt_rec(56.6);
        kpi.setJour(LocalDate.parse("2018-08-01"));
        kpi.setMois_annee("08-2018");
        kpi.setMtt_rec(67.8);
        kpi.setParc(89);
        kpi.setZone("Alaotra");

        Kpi kpi1=new Kpi();
        kpi1.setMtt_rec(7.9);
        kpi1.setActivation(7);
        kpi1.setCb_7j(8);
        kpi1.setCb_30j(7);
        kpi1.setCb_30jd(5);
        kpi1.setCumul_activation(89);
        kpi1.setCumul_mtt_rec(78.4);
        kpi1.setCumul_mtt_rec(56.6);
        kpi1.setJour(LocalDate.parse("2018-08-01"));
        kpi1.setMois_annee("08-2018");
        kpi1.setMtt_rec(67.8);
        kpi1.setParc(89);
        kpi1.setZone("Itasy");

        List<Kpi>kpis=Arrays.asList(kpi,kpi1);
        Mockito.when(kpiService.getAll(date)).thenReturn(Response.ok(kpis).build());
        given()
                .queryParam("date",date)
                .when().get("kpi")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body("[0].mtt_rec",is(kpi.getMtt_rec().floatValue()))
                .body("size()",is(kpis.size()));
        Mockito.verify(kpiService).getAll(any(String.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void getAllException(){
        String date="2018-08-01";
        Mockito.when(kpiService.getAll(any(String.class))).thenReturn(Response.serverError().build());
        given()
                .queryParam("date",date)
                .when().get("/kpi")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.verify(kpiService).getAll(any(String.class));
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
   void getAllDwhException(){
        String date="2018-08-01";
        Mockito.when(kpiService.getAllDwh(any(String.class))).thenReturn(Response.serverError().build());
        given()
                .queryParam("date",date)
                .when().get("/kpi/dwh")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
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
    @TestSecurity(authorizationEnabled = false)
    void getZoneException(){
        Mockito.when(kpiService.getZone()).thenReturn(Response.serverError().build());
        given()
                .when().get(kpiZoneUrl)
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.verify(kpiService).getZone();
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void sendSuccess() throws UnsupportedEncodingException {
        String tri="bom";
        String date="2018-08-01";
        Mockito.when(kpiService.sendSms(date,tri)).thenReturn(Response.ok().build());
        given()
                .header("tri",tri)
                .queryParam("date",date)
                .when().get("/kpi/send")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
        Mockito.verify(kpiService).sendSms(any(String.class),any(String.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void sendException() throws UnsupportedEncodingException{
        String tri="bom";
        String date="2018-08-01";
        Mockito.when(kpiService.sendSms(any(String.class),any(String.class))).thenReturn(Response.serverError().build());
        given()
                .header("tri",tri)
                .queryParam("date",date)
                .when().get("/kpi/send")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.verify(kpiService).sendSms(any(String.class),any(String.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void getHistoricSuccess() {
        String date="2018-08-01";
        Historic historic=new Historic();
        historic.setIdUser(1);
        historic.setKpiDate(LocalDate.parse("2018-08-01"));
        historic.setSendDate(LocalDate.parse("2018-08-02"));
        historic.setTriUser("bla");

        Historic historic1=new Historic();
        historic1.setIdUser(1);
        historic1.setKpiDate(LocalDate.parse("2018-08-25"));
        historic1.setSendDate(LocalDate.parse("2018-08-26"));
        historic1.setTriUser("bom");

        List<Historic> historics=Arrays.asList(historic,historic1);
        Mockito.when(kpiService.getHistoric(date)).thenReturn(Response.ok(historics).build());

        given()
                .queryParam("date",date)
                .when().get("/kpi/historic")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body("size()",is(historics.size()))
                .body("[0].triUser",is(historic.getTriUser()));
        Mockito.verify(kpiService).getHistoric(any(String.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void getHistoricException(){
        String date="2018-08-01";
        Mockito.when(kpiService.getHistoric(any(String.class))).thenReturn(Response.serverError().build());
        given()
                .queryParam("date",date)
                .when().get("/kpi/historic")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.verify(kpiService).getHistoric(any(String.class));
    }
}