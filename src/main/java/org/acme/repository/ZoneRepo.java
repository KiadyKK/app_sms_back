package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.entities.Zone;

import java.util.List;

@ApplicationScoped
public class ZoneRepo implements PanacheRepository<Zone> {
    public List<Zone> getAll(String name) {
        return find("name LIKE '%" + name + "%'").list();
    }
}
