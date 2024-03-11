package org.acme.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.acme.models.requests.AddRdzReq;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "rdz")
public class Rdz {
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
    private String tel;

    @Column
    private String email;

    @Column
    private int status;

    @ManyToOne
    @JoinColumn(name = "idZone")
    private Zone zone;

    public Rdz(AddRdzReq req, Zone zone) {
        this.tri = req.getTri();
        this.nom = req.getNom();
        this.prenom = req.getPrenom();
        this.tel = req.getTel();
        this.email = req.getEmail();
        this.status = 1;
        this.zone = zone;
    }
}
