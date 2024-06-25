package org.acme.resources;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.app_sms_833.Rdz;
import org.acme.requests.AddRdzReq;
import org.acme.services.RdzService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.List;
import static com.google.common.base.Predicates.equalTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
@QuarkusTest
class RdzResourceTest {
    @InjectMock
    RdzService rdzService;
    @Test
    void addRdzSuccess() {
        AddRdzReq req=new AddRdzReq();
        req.setEmail("john@gmail.com");
        req.setIdZone(1);
        req.setNom("John");
        req.setPrenom("Doe");
        req.setTel("0323232323");
        req.setTri("bom");
        req.setZone("Alaotra");

        Rdz rdz=new Rdz(req);
        Mockito.when(rdzService.addRdz(any(AddRdzReq.class))).thenReturn(Response.ok(rdz).build());

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .when().post("rdz")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body("tri",is(req.getTri()))
                        .body("nom",is(req.getNom()))
                                .body("prenom",is(req.getPrenom()))
                                        .body("tel",is(req.getTel()))
                                                .body("tri",is(req.getTri()))
                                                        .body("zone",is(req.getZone()));

        Mockito.verify(rdzService).addRdz(any(AddRdzReq.class));
    }
    @Test
    void addRdzException(){
        AddRdzReq req=new AddRdzReq();
        req.setEmail("john@gmail.com");
        req.setIdZone(1);
        req.setNom("John");
        req.setPrenom("Doe");
        req.setTel("0323232323");
        req.setTri("bom");
        req.setZone("Alaotra");
        Mockito.when(rdzService.addRdz(any(AddRdzReq.class))).thenReturn(Response.serverError().build());
        given()
                .body(req)
                .contentType(MediaType.APPLICATION_JSON)
                .when().post("/rdz")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.verify(rdzService).addRdz(any(AddRdzReq.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void getAllSuccess() {
        Rdz rdz=new Rdz();
        rdz.setEmail("john@gmail.com");
        rdz.setIdZone(1);
        rdz.setNom("John");
        rdz.setPrenom("Doe");
        rdz.setTel("0323232323");
        rdz.setTri("bom");
        rdz.setZone("Alaotra");

        Rdz rdz1=new Rdz();
        rdz1.setEmail("john@gmail.com");
        rdz1.setIdZone(1);
        rdz1.setNom("John");
        rdz1.setPrenom("Doe");
        rdz1.setTel("0323232323");
        rdz1.setTri("bom");
        rdz1.setZone("Alaotra");

        List<Rdz>rdzs= Arrays.asList(rdz,rdz1);

        Mockito.when(rdzService.getAll(any(String.class))).thenReturn(Response.ok(rdzs).build());
        String nom="";
        given()
                .queryParam("nom",nom)
                .when().get("rdz")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body("size()",is(rdzs.size()))
                .body("[0].tri",is(rdz.getTri()))
                .body("[1].nom",is(rdz1.getNom()));
        Mockito.verify(rdzService).getAll(any(String.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void getAllException(){
        String nom="";
        Mockito.when(rdzService.getAll(any(String.class))).thenReturn(Response.serverError().build());
        given().queryParam("nom",nom)
                .when().get("rdz")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.verify(rdzService).getAll(any(String.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void removeSuccess() {
        long id=1;
        Mockito.when(rdzService.delete(any(Long.class))).thenReturn(Response.noContent().build());
        given()
                .pathParam("id",id)
                .when().delete("/rdz/{id}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        Mockito.verify(rdzService).delete(any(Long.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void removeException(){
        long id=1;
        Mockito.when(rdzService.delete(any(long.class))).thenReturn(Response.serverError().build());

        given()
                .pathParam("id",id)
                .when().delete("/rdz/{id}")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.verify(rdzService).delete(any(Long.class));
    }
}