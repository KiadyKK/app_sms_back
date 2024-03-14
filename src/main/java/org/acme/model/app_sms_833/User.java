package org.acme.model.app_sms_833;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.acme.requests.AddUserReq;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String tri;

    @Column
    private String nom;

    @Column
    private String prenom;

    @Column
    private String email;

    @Column
    private String tel;

    @Column
    private int role;

    @Column
    private int status;

    public User(AddUserReq req) {
        this.tri = req.getTri();
        this.nom = req.getNom();
        this.prenom = req.getPrenom();
        this.email = req.getEmail();
        this.tel = req.getTel();
        this.role = 0;
        this.status = 1;
    }
}
