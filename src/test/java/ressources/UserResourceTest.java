package ressources;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.requests.LoginReq;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class UserResourceTest {
    @Test
    public void testSignUp() {
        LoginReq loginReq = new LoginReq();
        loginReq.setTri("admin");
        loginReq.setMdp("adminsms@oma");
        given()
                .contentType(ContentType.JSON)
                .body(loginReq)
                .when()
                .post("/user/login")
                .then()
                .statusCode(200)
                .body("token",notNullValue());
    }
}
