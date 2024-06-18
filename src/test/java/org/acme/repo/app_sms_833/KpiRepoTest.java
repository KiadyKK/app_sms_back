package org.acme.repo.app_sms_833;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.model.app_sms_833.Kpi;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
class KpiRepoTest {
@Inject
KpiRepo kpiRepo;
    @Test
    @TestTransaction
    void save() {
        Kpi kpi=new Kpi();
        kpi.setZone("Alaotra");
        kpi.setParc(5);
        kpi.setMtt_rec(7.9);
        kpi.setMois_annee("08-08");
        kpi.setJour(LocalDate.of(2021, 12, 25));
        kpi.setCumul_mtt_rec(89.9);
        kpi.setCumul_activation(87);
        kpi.setCb_30jd(67);
        kpi.setCb_30j(56);
        kpi.setCb_7j(78);
        kpi.setActivation(77);
        kpiRepo.save(kpi);

        Kpi retrievedKpi=kpiRepo.getByJour(LocalDate.of(2021, 12, 25));
        assertNotNull(retrievedKpi);
        assertEquals(LocalDate.of(2021, 12, 25), retrievedKpi.getJour());
    }

    @Test
    @TestTransaction
    void getByJour() {
        Kpi kpi=new Kpi();
        kpi.setZone("Alaotra");
        kpi.setParc(5);
        kpi.setMtt_rec(7.9);
        kpi.setMois_annee("08-08");
        kpi.setJour(LocalDate.of(2021, 12, 25));
        kpi.setCumul_mtt_rec(89.9);
        kpi.setCumul_activation(87);
        kpi.setCb_30jd(67);
        kpi.setCb_30j(56);
        kpi.setCb_7j(78);
        kpi.setActivation(77);
        kpiRepo.save(kpi);

        Kpi retrievedKpi = kpiRepo.getByJour(LocalDate.of(2021, 12, 25));
        assertNotNull(retrievedKpi);
        assertEquals(LocalDate.of(2021, 12, 25), retrievedKpi.getJour());
    }

    @Test
    @TestTransaction
    void getAll() {
        LocalDate date = LocalDate.of(2021, 12, 25);

        Kpi kpi1 = new Kpi();
        kpi1.setJour(date);
        kpiRepo.save(kpi1);

        Kpi kpi2 = new Kpi();
        kpi2.setJour(date);
        kpiRepo.save(kpi2);

        List<Kpi> kpis = kpiRepo.getAll(date);
        assertEquals(2, kpis.size());
    }

    @Test
    @TestTransaction
    void removeAll() {
        LocalDate date = LocalDate.of(2021, 12, 25);

        Kpi kpi1 = new Kpi();
        kpi1.setJour(date);
        kpiRepo.save(kpi1);

        Kpi kpi2 = new Kpi();
        kpi2.setJour(date);
        kpiRepo.save(kpi2);

        long removedCount = kpiRepo.removeAll(date);
        assertEquals(2, removedCount);

        List<Kpi> kpis = kpiRepo.getAll(date);
        assertTrue(kpis.isEmpty());
    }
}