package org.acme.model.app_sms_833;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "\"historic\"")
public class Historic {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="kpiDate")
    private LocalDate kpiDate;

    @Column(name="sendDate")
    private LocalDate sendDate;

    @Column(name="triUser")
    private String triUser;

    @Column(name="idUser")
    private long idUser;

    public Historic(LocalDate kpiDate, User user) {
        this.kpiDate = kpiDate;
        this.sendDate = LocalDate.now();
        this.idUser = user.getId();
        this.triUser = user.getTri();
    }
}
