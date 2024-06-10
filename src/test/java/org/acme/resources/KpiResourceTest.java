package org.acme.resources;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.dm_rf.DwhRes;
import org.acme.services.KpiService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.print.attribute.standard.Media;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
class KpiResourceTest {
    @InjectMock
    KpiService kpiService;
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
    void getAll() {
    }

    @Test
    void testJson() {
    }

    @Test
    void getAllDwh() {
    }

    @Test
    void getZone() {
    }

    @Test
    void send() {
    }

    @Test
    void getHistoric() {
    }
}