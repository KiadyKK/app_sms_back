package org.acme.model.app_sms_833;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.acme.model.dm_rf.DwhRes;
import java.time.LocalDate;
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "\"kpi\"")
public class Kpi {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="mois_annee")
    private String mois_annee;

    @Column(name="jour")
    private LocalDate jour;

    @Column(name="zone")
    private String zone;

    @Column(name="parc")
    private long parc;

    @Column(name="activation")
    private long activation;

    @Column(name="cumul_activation")
    private long cumul_activation;

    @Column(name="cb_30j")
    private long cb_30j;

    @Column(name="cb_7j")
    private long cb_7j;

    @Column(name="cb_30jd")
    private long cb_30jd;

    @Column(name="mtt_rec")
    private Double mtt_rec;

    @Column(name="cumul_mtt_rec")
    private Double cumul_mtt_rec;
    public Kpi(DwhRes dwhRes) {
        this.mois_annee = dwhRes.getMois_annee();
        this.jour = dwhRes.getJour();
        this.zone = dwhRes.getZone();
        this.parc = dwhRes.getParc();
        this.activation = dwhRes.getActivation();
        this.cumul_activation = dwhRes.getCumul_activation();
        this.cb_30j = dwhRes.getCb_30j();
        this.cb_7j = dwhRes.getCb_7j();
        this.cb_30jd = dwhRes.getCb_30jd();
        this.mtt_rec = dwhRes.getMtt_rec();
        this.cumul_mtt_rec = dwhRes.getCumul_mtt_rec();
    }
}
