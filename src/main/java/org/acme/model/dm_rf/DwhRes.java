package org.acme.model.dm_rf;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DwhRes {
    private String mois_annee;

    private LocalDate jour;

    private String zone;

    private long parc;

    private long activation;


//    private long cb_30jours;
//
//    private long cb_7jours;
//
//    private long cb_30jours_data;

    private long cumul_activation;

    private long cb_30j;

    private long cb_7j;

    private long cb_30jd;

    private Double mtt_rec;

    private Double cumul_mtt_rec;
}
