package org.acme.repo.app_sms_833;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.app_sms_833.Rdz;

import java.util.List;

@ApplicationScoped
public class RdzRepo implements PanacheRepository<Rdz> {
    public List<Rdz> getAll(String nom) {
        return find("nom LIKE '%" + nom + "%' AND status = 1").list();
    }

    public int remove(long id) {
        return update("status = 0 WHERE id = ?1", id);
    }
}
