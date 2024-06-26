package org.acme.repo.dm_rf;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.dm_rf.DwhRes;
import org.acme.model.dm_rf.Zone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DwhRepo {
    @Inject
    @DataSource("dm_rf")
    AgroalDataSource dataSource;

    public List<DwhRes> getAll(LocalDate startDate, LocalDate endDate) {

        String query1 = "SELECT\n" +
                "\tmois_annee,\n" +
                "\tupd_dt jour,\n" +
                "\tzone,\n" +
                "\tSUM(parc) parc,\n" +
                "\tSUM(delta_parc) delta_parc,\n" +
                "\tSUM(activation) activation,\n" +
                "\tSUM(cumul_activation) cumul_activation,  \n" +
                "\tSUM(cb_30jours) cb_30j,\n" +
                "\tSUM(cb_7jours) cb_7j, \n" +
                "\tSUM(cb_30jours_data) cb_30jd,\n" +
                "\tSUM(mtt_rec) mtt_rec,\n" +
                "\tSUM(cumul_mtt_rec) cumul_mtt_rec\n" +
                "FROM (\n" +
                "\tSELECT  \n" +
                "\t\tDATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "\t\tupd_dt,\n" +
                "\t\tzone,\n" +
                "\t\tSUM(qty_j_1) parc,\n" +
                "\t\tdelta delta_parc,\n" +
                "\t\t0 activation,\n" +
                "\t\t0 cumul_activation,  \n" +
                "\t\t0 cb_30jours,\n" +
                "\t\t0 cb_7jours, \n" +
                "\t\t0 cb_30jours_data,\n" +
                "\t\t0 mtt_rec,\n" +
                "\t\t0 cumul_mtt_rec\n" +
                "\tFROM(\n" +
                "\t\tSELECT \n" +
                "\t\t\t"+ endDate +" upd_dt, \n" +
                "\t\t\tSUM(IF(upd_dt='"+endDate+"',qty,0)) qty_j_1 ,\n" +
                "\t\t\tSUM(IF(upd_dt=DATE_SUB('"+ endDate +"', INTERVAL 1 DAY),qty,0)) qty_j_2, \n" +
                "\t\t\tIF(v3.sig_zoneorange_name_v3 IS NULL,'INCONNU',v3.sig_zoneorange_name_v3) zone,\n" +
                "\t\t\tSUM(IF(upd_dt='"+ endDate +"',qty,0))-SUM(IF(upd_dt=DATE_SUB('"+ endDate +"', INTERVAL 1 DAY),qty,0)) delta\n" +
                "\t\tFROM DM_OD.od_parc_orange od\n" +
                "\t\tLEFT JOIN DM_RF.`rf_sig_cell_krill_v3` v3 ON v3.sig_lac_ci=od.site_id\n" +
                "\t\tWHERE upd_dt BETWEEN  DATE_SUB('"+ endDate +"', INTERVAL 1 DAY) AND '"+ endDate +"' \n" +
                "\t\t\t\t\tAND parc_id = 5\n" +
                "\t\t\t\t\tAND billing_type IN (1)\n" +
                "\t\t\t\t\tAND statut IN (1,2,5)\n" +
                "\t\t\t\t\tGROUP BY v3.sig_zoneorange_name_v3\n" +
                "                \n" +
                "        ) t\n" +
                "\tGROUP BY upd_dt, zone\n" +
                "\t\n" +
                "\tUNION ALL\n" +
                "\t\n" +
                "\tSELECT  \n" +
                "\t\tDATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "\t\tupd_dt,\n" +
                "\t\tIF(rf.sig_zoneorange_name_v3 IS NULL,'INCONNU',rf.sig_zoneorange_name_v3) zone,\n" +
                "\t\t0 parc,\n" +
                "\t\t0 delta_parc,\n" +
                "\t\tSUM(IF(parc_id=1,qty,0)) activation,\n" +
                "\t\t0 cumul_activation,  \n" +
                "\t\t0 cb_30jours,\n" +
                "\t\t0 cb_7jours, \n" +
                "\t\t0 cb_30jours_data,\n" +
                "\t\t0 mtt_rec,\n" +
                "\t\t0 cumul_mtt_rec\n" +
                "\tFROM  DM_OD.od_parc_orange od \n" +
                "\tLEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_lac_ci\n" +
                "\tWHERE upd_dt = '"+ endDate +"'\n" +
                "\tAND billing_type IN (1,5)\n" +
                "\tAND statut = 1\n" +
                "\tGROUP BY upd_dt, zone\n" +
                "\t\n" +
                "\tUNION ALL\n" +
                "\t\n" +
                "\tSELECT  \n" +
                "\t\tDATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "\t\tMAX(upd_dt),\n" +
                "\t\tIF(rf.sig_zoneorange_name_v3 IS NULL,'INCONNU',rf.sig_zoneorange_name_v3) zone,\n" +
                "\t\t0 parc,\n" +
                "\t\t0 delta_parc,\n" +
                "\t\t0 activation,\n" +
                "\t\tSUM(IF(parc_id=1,qty,0)) cumul_activation,  \n" +
                "\t\t0 cb_30jours,\n" +
                "\t\t0 cb_7jours, \n" +
                "\t\t0 cb_30jours_data,\n" +
                "\t\t0 mtt_rec,\n" +
                "\t\t0 cumul_mtt_rec\n" +
                "\tFROM  DM_OD.od_parc_orange od \n" +
                "\tLEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_lac_ci\n" +
                "\tWHERE upd_dt BETWEEN '"+ startDate +"' AND '"+ endDate +"'\n" +
                "\tAND billing_type IN (1,5)\n" +
                "\tAND statut = 1\n" +
                "\tGROUP BY mois_annee, zone\n" +
                "\t\n" +
                "\tUNION ALL \n" +
                "\t\n" +
                "\tSELECT \n" +
                "\t\tDATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "\t\trec.upd_dt upd_dt,\n" +
                "\t\trec.zone zone,\n" +
                "\t\t0 parc,\n" +
                "\t\t0 delta_parc,\n" +
                "\t\t0 activation,\n" +
                "\t\t0 cumul_activation,  \n" +
                "\t\t0 cb_30jours,\n" +
                "\t\t0 cb_7jours, \n" +
                "\t\t0 cb_30jours_data,\n" +
                "\t\tSUM(amnt) mtt_rec,\n" +
                "\t\t0 cumul_mtt_rec\n" +
                "\tFROM\t\n" +
                "\t\t(\n" +
                "\t\t\tSELECT \n" +
                "\t\t\t\tupd_dt, \n" +
                "\t\t\t\tIFNULL(sg.sig_zoneorange_name_v3,'INCONNU') zone, \n" +
                "\t\t\t\tSUM(amount_htva) amnt\n" +
                "\t\t\tFROM `DM_CB`.`cb_recharge_qs` qs\n" +
                "\t\t\tLEFT JOIN DM_RF.rf_sig_cell_krill_v3 sg ON sg.sig_cel=qs.cel AND sg.sig_lac=qs.lac\n" +
                "\t\t\tWHERE upd_dt = '"+ endDate +"' AND billing_type = 1\n" +
                "\t\t\tGROUP BY upd_dt, sg.sig_zoneorange_name_v3\n" +
                "\t\t)rec \n" +
                "\t\tGROUP BY zone\n" +
                "\t\t\n" +
                "\tUNION ALL\n" +
                "\n" +
                "\tSELECT \n" +
                "\t\tcumul_rec.periode mois_annee,\n" +
                "\t\tcumul_rec.upd_dt upd_dt,\n" +
                "\t\tcumul_rec.zone zone,\n" +
                "\t\t0 parc,\n" +
                "\t\t0 delta_parc,\n" +
                "\t\t0 activation,\n" +
                "\t\t0 cumul_activation,  \n" +
                "\t\t0 cb_30jours,\n" +
                "\t\t0 cb_7jours, \n" +
                "\t\t0 cb_30jours_data,\n" +
                "\t\t0 mtt_rec,\n" +
                "\t\tSUM(amnt) cumul_mtt_rec\n" +
                "\tFROM\t\n" +
                "\t\t(\n" +
                "\t\t\tSELECT \n" +
                "\t\t\t\tDATE_FORMAT(upd_dt, '%m-%Y') periode,\n" +
                "\t\t\t\tMAX(upd_dt) upd_dt, \n" +
                "\t\t\t\tIFNULL(sg.sig_zoneorange_name_v3,'INCONNU') zone, \n" +
                "\t\t\t\tSUM(amount_htva) amnt\n" +
                "\t\t\tFROM `DM_CB`.`cb_recharge_qs` qs\n" +
                "\t\t\tLEFT JOIN DM_RF.rf_sig_cell_krill_v3 sg ON sg.sig_cel=qs.cel AND sg.sig_lac=qs.lac\n" +
                "\t\t\tWHERE upd_dt BETWEEN  '"+ startDate +"' AND '"+ endDate +"' AND billing_type = 1\n" +
                "\t\t\tGROUP BY periode, sg.sig_zoneorange_name_v3 \n" +
                "\t\t)cumul_rec\n" +
                "\tGROUP BY zone\n" +
                "\t\t\n" +
                "\tUNION ALL\n" +
                "\t\t\n" +
                "\t#Charged Base\n" +
                "\tSELECT\n" +
                "\t\tDATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "\t\tupd_dt,\n" +
                "\t\tIF(rf.sig_zoneorange_name_v3 IS NULL,'INCONNU',rf.sig_zoneorange_name_v3) zone,\n" +
                "\t\t0 parc,\n" +
                "\t\t0 delta_parc,\n" +
                "\t\t0 activation,\n" +
                "\t\t0 cumul_activation,  \n" +
                "\t\tSUM(qty2) cb_30jours,\n" +
                "\t\tSUM(qty4) cb_7jours, \n" +
                "\t\tSUM(qty3) cb_30jours_data,\n" +
                "\t\t0 mtt_rec,\n" +
                "\t\t0 cumul_mtt_rec\n" +
                "\tFROM DM_OD.od_parc_orange od \n" +
                "\tLEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_lac_ci\n" +
                "\tWHERE upd_dt = '"+ endDate +"'\n" +
                "\t\tAND statut <> 4\n" +
                "\t\tAND billing_type = 1\n" +
                "\tGROUP BY \n" +
                "\t\tupd_dt, zone\n" +
                "\n" +
                ") table_finale\n" +
                "GROUP BY jour,zone;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query1)) {
            List<DwhRes> dwhResList = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery(query1)) {
                while (rs.next()){
                    DwhRes dwhRes = new DwhRes();
                    dwhRes.setMois_annee(rs.getString("mois_annee"));
                    dwhRes.setJour(LocalDate.parse(rs.getString("jour")));
                    dwhRes.setZone(rs.getString("zone"));
                    dwhRes.setParc(rs.getLong("parc"));
                    dwhRes.setActivation(rs.getLong("activation"));
                    dwhRes.setCumul_activation(rs.getLong("cumul_activation"));
                    dwhRes.setCb_30j(rs.getLong("cb_30j"));
                    dwhRes.setCb_7j(rs.getLong("cb_7j"));
                    dwhRes.setCb_30jd(rs.getLong("cb_30jd"));
                    dwhRes.setMtt_rec(rs.getDouble("mtt_rec"));
                    dwhRes.setCumul_mtt_rec(rs.getDouble("cumul_mtt_rec"));
                    dwhResList.add(dwhRes);
                }
            }
            return dwhResList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Zone> getAllZone() {
        String query = "SELECT * FROM DM_RF.rf_sig_zone_v3";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            List<Zone> zoneList = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Zone zone = new Zone();
                    zone.setId(rs.getLong("sig_id_zone"));
                    zone.setName(rs.getString("sig_zone"));
                    zoneList.add(zone);
                }
            }
            return zoneList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
