package org.acme.model.app_sms_833;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.acme.requests.AddUserReq;
import org.mindrot.jbcrypt.BCrypt;

@Getter
@Setter
@NoArgsConstructor
@Entity
//@Table(name = "user")
@Table(name = "\"user\"")
public class User {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="tri")
    private String tri;

    @Column(name="mdp")
    private String mdp;

    @Column(name="nom")
    private String nom;

    @Column(name="prenom")
    private String prenom;

    @Column(name="email")
    private String email;

    @Column(name="tel")
    private String tel;

    @Column(name="role")
    private int role;

    @Column(name="status")
    private int status;

    public User(AddUserReq req) {
        this.tri = req.getTri();
        this.mdp = BCrypt.hashpw(req.getTri(), BCrypt.gensalt());
        this.nom = req.getNom();
        this.prenom = req.getPrenom();
        this.email = req.getEmail();
        this.tel = req.getTel();
        this.role = 0;
        this.status = 1;
    }
}
