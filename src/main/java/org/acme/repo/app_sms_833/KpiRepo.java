package org.acme.repo.app_sms_833;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.model.app_sms_833.Kpi;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class KpiRepo implements PanacheRepository<Kpi> {
    @Transactional
    public void save(Kpi kpi) {
        persist(kpi);
    }

    public Kpi getByJour(LocalDate jour) {
        return find("jour = ?1", jour).firstResult();
    }

    public List<Kpi> getAll(LocalDate date) {
        return find("jour = ?1", date).list();
    }

    @Transactional
    public long removeAll(LocalDate date) {
        return delete("jour = ?1", date);
    }
}
