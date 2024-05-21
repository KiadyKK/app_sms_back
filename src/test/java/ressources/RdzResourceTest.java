package ressources;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.acme.requests.LoginReq;
import org.acme.services.RdzService;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.gradle.internal.impldep.com.google.common.base.Predicates.equalTo;

@QuarkusTest
public class RdzResourceTest {

    public String getToken() {
        LoginReq loginReq = new LoginReq();
        loginReq.setTri("admin");
        loginReq.setMdp("adminsms@oma");
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(loginReq)
                        .when()
                        .post("/user/login");
        return response.then().extract().path("token");
    }

   /* @Test
   void TestListRdzEndpoint(){
       String token=getToken();
        System.out.println(token);
       given()
               .header("Authorization","Bearer "+token)
               .queryParam("nom","")
               .when().get("/rdz")
               .then()
               .statusCode(200);
   }

    */
}

