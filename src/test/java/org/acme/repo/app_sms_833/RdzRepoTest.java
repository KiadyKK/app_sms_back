package org.acme.repo.app_sms_833;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.model.app_sms_833.Rdz;
import org.acme.requests.AddRdzReq;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
class RdzRepoTest {
    @Inject
    RdzRepo rdzRepo;
    @Test
    @TestTransaction
    void getAll() {
        String nom="";
        AddRdzReq req=new AddRdzReq();
        req.setEmail("john@gmail.com");
        req.setIdZone(1);
        req.setNom("john");
        req.setPrenom("doe");
        req.setTel("0323232323");
        req.setTri("bom");
        req.setZone("Alaotra");

        Rdz rdz=new Rdz(req);
        rdzRepo.persist(rdz);

        AddRdzReq req1=new AddRdzReq();
        req1.setEmail("john@gmail.com");
        req1.setIdZone(1);
        req1.setNom("john");
        req1.setPrenom("doe");
        req1.setTel("0323232323");
        req1.setTri("bom");
        req1.setZone("Alaotra");

        Rdz rdz1=new Rdz(req1);
        rdzRepo.persist(rdz1);

        List<Rdz>rdzs=rdzRepo.getAll(nom);
        assertEquals(2,rdzs.size());
    }

    @Test
    @TestTransaction
    void remove() {


        AddRdzReq req=new AddRdzReq();
        req.setEmail("john@gmail.com");
        req.setIdZone(1);
        req.setNom("john");
        req.setPrenom("doe");
        req.setTel("0323232323");
        req.setTri("bom");
        req.setZone("Alaotra");

        Rdz rdz=new Rdz(req);
        rdzRepo.persist(rdz);

        long id=rdz.getId();

        int removedRdz=rdzRepo.remove(id);
        assertEquals(1,removedRdz);
    }
}