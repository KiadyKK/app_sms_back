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
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
    Mockito.reset(rdzRepo);
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
    AddRdzReq addRdzReq1=new AddRdzReq();
    addRdzReq1.setEmail("rakoto@gmail.com");
    addRdzReq1.setIdZone(1);
    addRdzReq1.setNom("Mimie");
    addRdzReq1.setPrenom("Andria");
    addRdzReq1.setTel("0989878765");
    addRdzReq1.setTri("dny");
    addRdzReq1.setZone("Alaotra");
    rdz2=new Rdz(addRdzReq1);
    listMock.add(rdz1);
    listMock.add(rdz2);
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

 @Test
 void addRdzTest(){
    AddRdzReq req=new AddRdzReq();
    req.setEmail("rakoto@gmail.com");
    req.setIdZone(1);
    req.setNom("RAKOTOVAO");
    req.setPrenom("Michelle");
    req.setTel("0345415027");
    req.setTri("iol");
    req.setZone("Alaotra");
    Rdz rdz=new Rdz(req);
    doNothing().when(rdzRepo).persist(any(Rdz.class));
    Response response=rdzService.addRdz(req);
    assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    assertNotNull(response);
    assertNotNull(response.getEntity());

    Rdz entity=(Rdz) response.getEntity();
    assertEquals(rdz.getPrenom(),entity.getPrenom());
    assertEquals(rdz.getNom(),entity.getNom());
    assertEquals(rdz.getTri(),entity.getTri());
    assertEquals(rdz.getEmail(),entity.getEmail());
    assertEquals(rdz.getZone(),entity.getZone());
    assertEquals(rdz.getTel(),entity.getTel());
    verify(rdzRepo).persist(any(Rdz.class));
 }

    @Test
    void deleteRdz() {
        long idToDelete = 1L;
        AddRdzReq req = new AddRdzReq();
        req.setEmail("rak@gmail.com");
        req.setIdZone(1);
        req.setNom("Michelle");
        req.setPrenom("Rakoto");
        req.setTel("0989876479");
        req.setTri("iol");
        req.setZone("Alaotra");
        rdz1 = new Rdz(req);
        rdz1.setId(idToDelete);

        when(rdzRepo.remove(idToDelete)).thenReturn(rdz1.getStatus());
        Response response = rdzService.delete(idToDelete);
        verify(rdzRepo, times(1)).remove(idToDelete);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
}