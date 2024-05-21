package services;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.model.app_sms_833.Rdz;
import org.acme.repo.app_sms_833.RdzRepo;
import org.acme.requests.AddRdzReq;
import org.acme.services.RdzService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@QuarkusTest
public class RdzServiceTest {
 @InjectMock
    RdzRepo rdzRepo;
 @Inject
    RdzService rdzService;
 private Rdz rdz1;
 private Rdz rdz2;
 private List<Rdz> listMock;
@BeforeEach
public void setup(){
    AddRdzReq addRdzReq=new AddRdzReq();
    listMock=new ArrayList<>();
    addRdzReq.setEmail("rak@gmail.com");
    addRdzReq.setIdZone(1);
    addRdzReq.setNom("Michelle");
    addRdzReq.setPrenom("Rakoto");
    addRdzReq.setTel("0989876479");
    addRdzReq.setTri("iol");
    addRdzReq.setZone("Alaotra");
    rdz1=new Rdz(addRdzReq);
     rdzRepo.persist(rdz1);
    AddRdzReq addRdzReq1=new AddRdzReq();
    addRdzReq1.setEmail("rakoto@gmail.com");
    addRdzReq1.setIdZone(1);
    addRdzReq1.setNom("Mimie");
    addRdzReq1.setPrenom("Andria");
    addRdzReq1.setTel("0989878765");
    addRdzReq1.setTri("dny");
    addRdzReq1.setZone("Alaotra");
    rdz2=new Rdz(addRdzReq1);
    rdzRepo.persist(rdz2);
  //  List<Rdz> rdzList=new ArrayList<>();
    listMock.add(rdz1);
    listMock.add(rdz2);
    //Mockito.when(Mockito.mock(rdzRepo.getAll(""))).thenReturn(rdzList);
    when(rdzRepo.getAll("")).thenReturn(listMock);
}
 @Test
    void getAllRdzTest(){
    Response response=rdzService.getAll("");
    assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    assertNotNull(response);
    assertNotNull(response.getEntity());
    List<Rdz> entity=(List<Rdz>)response.getEntity();
    assertFalse(entity.isEmpty());
    assertEquals(2,entity.size());
    assertEquals("iol",entity.get(0).getTri());
    assertEquals("dny",entity.get(1).getTri());
 }

}

