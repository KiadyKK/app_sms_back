package org.acme.repo.app_sms_833;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.model.app_sms_833.Historic;
import java.time.LocalDate;
import java.util.List;
@ApplicationScoped
public class HistoricRepo implements PanacheRepository<Historic> {
    public List<Historic> getAll(LocalDate date) {
        return find("sendDate = ?1", date).list();
    }

    @Transactional
    public void save(Historic historic) {
        persist(historic);
    }
}
