package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.entities.User;

import java.util.List;

@ApplicationScoped
public class UserRepo implements PanacheRepository<User> {
    public User findByTri(String tri) {
        return find("tri = ?1", tri).firstResult();
    }

    public List<User> getAll(String nom) {
        return find("nom LIKE '%" + nom + "%' AND status = 1").list();
    }

    public int remove(long id) {
        return update("status = 0 WHERE id = ?1", id);
    }
}
