package org.acme.resources;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.app_sms_833.Rdz;
import org.acme.requests.AddRdzReq;
import org.acme.services.RdzService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
@QuarkusTest
@TestHTTPEndpoint(RdzResource.class)
class RdzResourceTest {
    @InjectMock
    RdzService rdzService;
    @Test
    void addRdz() {

    }
    @Test
    void getAll() {
    }

    @Test
    void remove() {
    }
}