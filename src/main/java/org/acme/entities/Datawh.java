package org.acme.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "datawh")
public class Datawh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private long activation;

    @Column
    private long cumulActivation;

    @Column
    private long cumulMontantRechargement;

    @Column
    private long cumulNbRechargement;

    @Column
    private LocalDate jour;

    @Column
    private String moisAnnee;

    @Column
    private long montantRechargement;

    @Column
    private long nombreRechargement;

    @Column
    private long parc;

    @Column
    private String zone;
}
