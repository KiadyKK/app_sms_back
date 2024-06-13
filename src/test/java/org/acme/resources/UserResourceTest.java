package org.acme.resources;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.app_sms_833.User;
import org.acme.requests.AddUserReq;
import org.acme.requests.LoginReq;
import org.acme.requests.PutPasswordReq;
import org.acme.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
@QuarkusTest
class UserResourceTest {
    @InjectMock
    UserService userService;
    @Test
    void signUpSuccess() {
        LoginReq req=new LoginReq();
        req.setTri("bom");
        req.setMdp("bom");

        User user=new User();
        user.setTri("bom");
        user.setTel("0345415027");

        JsonObject object=new JsonObject();
        object.put("user",user).put("token","Bearer token");

        Mockito.when(userService.login(any(LoginReq.class))).thenReturn(Response.ok(object).build());
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .when().post("/user/login")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body("user.tri", is("bom"))
                .body("token", startsWith("Bearer "));
        Mockito.verify(userService).login(any(LoginReq.class));
    }
    @Test
    void loginUserNotFound(){
        LoginReq req=new LoginReq();
        req.setTri("bom");
        req.setMdp("bom");

        Mockito.when(userService.login(any(LoginReq.class))).
                thenReturn(Response.status(Response.Status.NOT_FOUND.getStatusCode()).
                entity("User Not found. Contact DSI support").
                build());
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .when().post("/user/login")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .body(is("User Not found. Contact DSI support"));
        Mockito.verify(userService).login(any(LoginReq.class));
    }
    @Test
    void loginInvalidPassword(){
        LoginReq req=new LoginReq();
        req.setTri("bom");
        req.setMdp("bom");

        Mockito.when(userService.login(any(LoginReq.class))).
                thenReturn(Response.status(Response.Status.UNAUTHORIZED.getStatusCode())
                .entity("Invalid Password !").build());
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .when().post("/user/login")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .body(is("Invalid Password !"));
    Mockito.verify(userService).login(any(LoginReq.class));
    }
    @Test
    void loginServerError(){
        LoginReq req=new LoginReq();
        req.setTri("bom");
        req.setMdp("bom");

        Mockito.when(userService.login(any(LoginReq.class))).thenReturn(Response.serverError().build());
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .when().post("/user/login")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.verify(userService).login(any(LoginReq.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void addUserSuccess() {
        AddUserReq req=new AddUserReq();
        req.setTri("bom");
        req.setTel("0323232323");
        req.setPrenom("John");
        req.setNom("Doe");
        req.setEmail("john@gmail.com");

        User user=new User(req);

        Mockito.when(userService.addUser(any(AddUserReq.class))).thenReturn(Response.ok(user).build());
        given()
                .body(req)
                .contentType(MediaType.APPLICATION_JSON)
                .when().post("/user/new")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body("tri",is(user.getTri()))
                .body("email",is(user.getEmail()));
        Mockito.verify(userService).addUser(any(AddUserReq.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void addUserException(){
        AddUserReq req=new AddUserReq();
        req.setTri("bom");
        req.setTel("0323232323");
        req.setPrenom("John");
        req.setNom("Doe");
        req.setEmail("john@gmail.com");

        Mockito.when(userService.addUser(any(AddUserReq.class))).thenReturn(Response.serverError().build());
        given()
                .body(req)
                .contentType(MediaType.APPLICATION_JSON)
                .when().post("/user/new")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.verify(userService).addUser(any(AddUserReq.class));
    }
    @Test
    void putMdpSuccess() {
        PutPasswordReq req = new PutPasswordReq();
        req.setTrigramme("bom");
        req.setPassword("oldPassword");
        req.setNewPassword("newPassword");

        Mockito.when(userService.modifyMdp(any(PutPasswordReq.class))).thenReturn(true);

        given()
                .body(req)
                .contentType(MediaType.APPLICATION_JSON)
                .when().put("/user/putMdp")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
        Mockito.verify(userService).modifyMdp(any(PutPasswordReq.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void putMdpFailure() {
        PutPasswordReq req = new PutPasswordReq();
        req.setTrigramme("bom");
        req.setPassword("wrongPassword");
        req.setNewPassword("newPassword");

        Mockito.when(userService.modifyMdp(any(PutPasswordReq.class))).thenReturn(false);

        given()
                .body(req)
                .contentType(MediaType.APPLICATION_JSON)
                .when().put("/user/putMdp")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
        Mockito.verify(userService).modifyMdp(any(PutPasswordReq.class));
    }

    @Test
    @TestSecurity(authorizationEnabled = false)
    void getAllSuccess() {
        AddUserReq req=new AddUserReq();
        req.setTri("bom");
        req.setTel("0323232323");
        req.setPrenom("John");
        req.setNom("Doe");

        AddUserReq req1=new AddUserReq();
        req1.setTri("bom");
        req1.setTel("0323232323");
        req1.setPrenom("John");
        req1.setNom("Doe");
        req1.setEmail("john@gmail.com");

        User user1=new User(req1);
        User user=new User(req);

        String nom="";
        List<User>users= Arrays.asList(user,user1);
        Mockito.when(userService.getAll(any(String.class))).thenReturn(Response.ok(users).build());

        given().queryParam("nom",nom)
                .when().get("/user")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("size()",is(users.size()))
                .body("[0].nom",is(user.getNom()))
                .body("[1].tri",is(user1.getTri()));
        Mockito.verify(userService).getAll(any(String.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void getAllFailure(){
        String nom="";
        Mockito.when(userService.getAll(any(String.class))).thenReturn(Response.serverError().build());
        given().queryParam("nom",nom)
                .when().get("/user")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.verify(userService).getAll(any(String.class));
    }

    @Test
    @TestSecurity(authorizationEnabled = false)
    void removeSuccess() {
        long id=1;
        Mockito.when(userService.delete(any(Long.class))).thenReturn(Response.noContent().build());
        given()
                .pathParam("id",id)
                .when().delete("/user/{id}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        Mockito.verify(userService).delete(any(Long.class));
    }
    @Test
    @TestSecurity(authorizationEnabled = false)
    void removeException(){
        long id=1;
        Mockito.when(userService.delete(any(Long.class))).thenReturn(Response.serverError().build());
        given()
                .pathParam("id",id)
                .when().delete("/user/{id}")
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        Mockito.verify(userService).delete(any(Long.class));
    }
}