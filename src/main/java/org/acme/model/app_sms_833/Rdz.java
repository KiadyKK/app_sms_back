package org.acme.model.app_sms_833;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.acme.requests.AddRdzReq;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "\"rdz\"")
public class Rdz  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name="tri")
    private String tri;

    @Column(name="nom")
    private String nom;

    @Column(name="prenom")
    private String prenom;

    @Column(name="tel")
    private String tel;

    @Column(name="email")
    private String email;

    @Column(name="status")
    private int status;

    @Column(name="idZone")
    private long idZone;

    @Column(name="zone")
    private String zone;

    public Rdz(AddRdzReq req) {
        this.tri = req.getTri();
        this.nom = req.getNom();
        this.prenom = req.getPrenom();
        this.tel = req.getTel();
        this.email = req.getEmail();
        this.status = 1;
        this.idZone = req.getIdZone();
        this.zone = req.getZone();
    }
}
