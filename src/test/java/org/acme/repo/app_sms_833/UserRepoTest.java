package org.acme.repo.app_sms_833;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.model.app_sms_833.User;
import org.acme.requests.AddUserReq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
class UserRepoTest {
    @Inject
    UserRepo userRepo;

    @TestTransaction
    @Test
    void findByTri() {
        AddUserReq req=new AddUserReq();
        req.setTri("bom");
        req.setTel("0323232323");
        req.setPrenom("Doe");
        req.setNom("John");
        req.setEmail("john@gmail.com");
        User user=new User(req);
        userRepo.persist(user);
        String tri=user.getTri();
        User userToFind=userRepo.findByTri(tri);

        assertEquals(user.getTri(),userToFind.getTri());
        assertEquals(user.getPrenom(),userToFind.getPrenom());
    }
    @TestTransaction
    @Test
    void getAll() {

        AddUserReq req=new AddUserReq();
        req.setTri("bom");
        req.setTel("0323232323");
        req.setPrenom("Doe");
        req.setNom("John");
        req.setEmail("john@gmail.com");
        User user=new User(req);
        userRepo.persist(user);

        AddUserReq req1=new AddUserReq();
        req1.setTri("tst");
        req1.setTel("0323232322");
        req1.setPrenom("Baume");
        req1.setNom("Fosa");
        req1.setEmail("baume@gmail.com");
        User user1=new User(req);
        userRepo.persist(user1);

        String nom="";
        List<User>users=userRepo.getAll(nom);

        assertEquals(2,users.size());

    }
    @TestTransaction
    @Test
    void remove() {
        AddUserReq req=new AddUserReq();
        req.setTri("bom");
        req.setTel("0323232323");
        req.setPrenom("Doe");
        req.setNom("John");
        req.setEmail("john@gmail.com");
        User user=new User(req);
        userRepo.persist(user);

        long id=user.getId();

        int removedUser=userRepo.remove(id);
        assertEquals(1,removedUser);
    }
}