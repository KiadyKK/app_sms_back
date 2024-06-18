package org.acme.middleware;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.acme.model.app_sms_833.User;
import org.acme.repo.app_sms_833.KpiRepo;
import org.acme.repo.app_sms_833.UserRepo;
import org.acme.repo.dm_rf.DwhRepo;
import org.acme.requests.AddUserReq;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class DwhCronTest {
    @InjectMock
    MailShared mailShared;
    @InjectMock
    KpiRepo kpiRepo;
    @InjectMock
    UserRepo userRepo;
    @InjectMock
    DwhRepo dwhRepo;
    @Inject
    DwhCron dwhCron;
    @Test
    void testLoadDwhData(){
       /* AddUserReq req=new AddUserReq();
        req.setEmail("john@gmail.com");
        req.setNom("John");
        req.setPrenom("Doe");
        req.setTel("0323232323");
        req.setTri("bom");
        User user=new User(req);

        when(userRepo.findAll()).thenReturn((PanacheQuery<User>) Collections.singletonList(user));
        when(dwhRepo.getAll(any(LocalDate.class), any(LocalDate.class))).thenReturn(Collections.emptyList());

        dwhCron.loadDwhData();

        */
    }
}