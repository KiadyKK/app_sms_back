package org.acme.repo.app_sms_833;
import io.quarkus.test.TestTransaction;
import jakarta.inject.Inject;
import io.quarkus.test.junit.QuarkusTest;
import org.acme.model.app_sms_833.Historic;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
class HistoricRepoTest {
    @Inject
    HistoricRepo historicRepo;
    @Test
    @TestTransaction
    public void testSave() {
        Historic historic = new Historic();
        historic.setSendDate(LocalDate.now());
        historicRepo.save(historic);

        List<Historic> historics = historicRepo.findAll().list();
        assertFalse(historics.isEmpty());
        assertEquals(historic.getSendDate(), historics.get(0).getSendDate());
    }
    @Test
    @TestTransaction
    void testGetAll(){
        LocalDate date=(LocalDate.of(2018,03,14));
        Historic historic=new Historic();
        historic.setIdUser(2);
        historic.setKpiDate(LocalDate.of(2018,03,13));
        historic.setSendDate(date);
        historic.setTriUser("bom");
        historicRepo.save(historic);

        Historic historic1=new Historic();
        historic1.setIdUser(3);
        historic1.setKpiDate(LocalDate.of(2018,03,13));
        historic1.setSendDate(date);
        historic1.setTriUser("bom");
        historicRepo.save(historic1);

        List<Historic>historics=historicRepo.getAll(date);
        assertEquals(2,historics.size());
    }
}