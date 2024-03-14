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
@Table(name = "kpi")
public class Kpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String mois_annee;

    @Column
    private LocalDate jour;

    @Column
    private String zone;

    @Column
    private long parc;

    @Column
    private long activation;

    @Column
    private long cumul_activation;

    @Column
    private long nb_rec;

    @Column
    private long cumul_nb_rec;

    @Column
    private Double mtt_rec;

    @Column
    private Double cumul_mtt_rec;

    public Kpi(DwhRes dwhRes) {
        this.mois_annee = dwhRes.getMois_annee();
        this.jour = dwhRes.getJour();
        this.zone = dwhRes.getZone();
        this.parc = dwhRes.getParc();
        this.activation = dwhRes.getActivation();
        this.cumul_activation = dwhRes.getCumul_activation();
        this.nb_rec = dwhRes.getNb_rec();
        this.cumul_nb_rec = dwhRes.getCumul_nb_rec();
        this.mtt_rec = dwhRes.getMtt_rec();
        this.cumul_mtt_rec = dwhRes.getMtt_rec();
    }
}
