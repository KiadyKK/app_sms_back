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

    public List<DwhRes> getAll(LocalDate end) {
        LocalDate start = end.withDayOfMonth(1);

        String query = "SELECT\n" +
                "mois_annee,\n" +
                "upd_dt jour,\n" +
                "zone,\n" +
                "SUM(parc) parc,\n" +
                "SUM(delta_parc) delta_parc,\n" +
                "SUM(activation) activation,\n" +
                "SUM(cumul_activation) cumul_activation,  \n" +
                "SUM(cb_30jours) cb_30j,\n" +
                "SUM(cb_7jours) cb_7j, \n" +
                "SUM(cb_30jours_data) cb_30jd,\n" +
                "SUM(mtt_rec) mtt_rec,\n" +
                "SUM(cumul_mtt_rec) cumul_mtt_rec\n" +
                "FROM (\n" +
                "SELECT  \n" +
                "DATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "upd_dt,\n" +
                "zone,\n" +
                "SUM(qty_j_1) parc,\n" +
                "delta delta_parc,\n" +
                "0 activation,\n" +
                "0 cumul_activation,  \n" +
                "0 cb_30jours,\n" +
                "0 cb_7jours, \n" +
                "0 cb_30jours_data,\n" +
                "0 mtt_rec,\n" +
                "0 cumul_mtt_rec\n" +
                "FROM(\n" +
                "SELECT \n" +
                "'" + end + "' upd_dt, \n" +
                "SUM(IF(upd_dt='" + end + "',qty,0)) qty_j_1 ,\n" +
                "SUM(IF(upd_dt=DATE_SUB('" + end + "', INTERVAL 1 DAY),qty,0)) qty_j_2, \n" +
                "IF(v3.sig_zoneorange_name_v3 IS NULL,'INCONNU',v3.sig_zoneorange_name_v3) zone,\n" +
                "SUM(IF(upd_dt='" + end + "',qty,0))-SUM(IF(upd_dt=DATE_SUB('" + end + "', INTERVAL 1 DAY),qty,0)) delta\n" +
                "FROM DM_OD.od_parc_orange od\n" +
                "LEFT JOIN DM_RF.`rf_sig_cell_krill_v3` v3 ON v3.sig_lac_ci=od.site_id\n" +
                "WHERE upd_dt BETWEEN  DATE_SUB('" + end + "', INTERVAL 1 DAY) AND '" + end + "' \n" +
                "AND parc_id = 5\n" +
                "AND billing_type IN (1)\n" +
                "AND statut IN (1,2,5)\n" +
                "GROUP BY v3.sig_zoneorange_name_v3\n" +
                ") t\n" +
                "GROUP BY upd_dt, zone\n" +
                "\n" +
                "UNION ALL\n" +
                "\n" +
                "SELECT  \n" +
                "DATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "upd_dt,\n" +
                "IF(rf.sig_zoneorange_name_v3 IS NULL,'INCONNU',rf.sig_zoneorange_name_v3) zone,\n" +
                "0 parc,\n" +
                "0 delta_parc,\n" +
                "SUM(IF(parc_id=1,qty,0)) activation,\n" +
                "0 cumul_activation,  \n" +
                "0 cb_30jours,\n" +
                "0 cb_7jours, \n" +
                "0 cb_30jours_data,\n" +
                "0 mtt_rec,\n" +
                "0 cumul_mtt_rec\n" +
                "FROM  DM_OD.od_parc_orange od \n" +
                "LEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_lac_ci\n" +
                "WHERE upd_dt = '" + end + "'\n" +
                "AND billing_type IN (1,5)\n" +
                "AND statut = 1\n" +
                "GROUP BY upd_dt, zone\n" +
                "\n" +
                "UNION ALL\n" +
                "\n" +
                "SELECT  \n" +
                "DATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "MAX(upd_dt),\n" +
                "IF(rf.sig_zoneorange_name_v3 IS NULL,'INCONNU',rf.sig_zoneorange_name_v3) zone,\n" +
                "0 parc,\n" +
                "0 delta_parc,\n" +
                "0 activation,\n" +
                "SUM(IF(parc_id=1,qty,0)) cumul_activation,  \n" +
                "0 cb_30jours,\n" +
                "0 cb_7jours, \n" +
                "0 cb_30jours_data,\n" +
                "0 mtt_rec,\n" +
                "0 cumul_mtt_rec\n" +
                "FROM  DM_OD.od_parc_orange od \n" +
                "LEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_lac_ci\n" +
                "WHERE upd_dt BETWEEN '" + start + "' AND '" + end + "'\n" +
                "AND billing_type IN (1,5)\n" +
                "AND statut = 1\n" +
                "GROUP BY mois_annee, zone\n" +
                "\n" +
                "UNION ALL \n" +
                "\n" +
                "SELECT \n" +
                "DATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "rec.upd_dt upd_dt,\n" +
                "rec.zone zone,\n" +
                "0 parc,\n" +
                "0 delta_parc,\n" +
                "0 activation,\n" +
                "0 cumul_activation,  \n" +
                "0 cb_30jours,\n" +
                "0 cb_7jours, \n" +
                "0 cb_30jours_data,\n" +
                "SUM(amnt) mtt_rec,\n" +
                "0 cumul_mtt_rec\n" +
                "FROM\t\n" +
                "(\n" +
                "SELECT \n" +
                "upd_dt, \n" +
                "IFNULL(sg.sig_zoneorange_name_v3,'INCONNU') zone, \n" +
                "SUM(amount_htva) amnt\n" +
                "FROM `DM_CB`.`cb_recharge_qs` qs\n" +
                "LEFT JOIN DM_RF.rf_sig_cell_krill_v3 sg ON sg.sig_cel=qs.cel AND sg.sig_lac=qs.lac\n" +
                "WHERE upd_dt = '" + end + "' AND billing_type = 1\n" +
                "GROUP BY upd_dt, sg.sig_zoneorange_name_v3\n" +
                ")rec \n" +
                "GROUP BY zone\n" +
                "\n" +
                "UNION ALL\n" +
                "\n" +
                "SELECT \n" +
                "cumul_rec.periode mois_annee,\n" +
                "cumul_rec.upd_dt upd_dt,\n" +
                "cumul_rec.zone zone,\n" +
                "0 parc,\n" +
                "0 delta_parc,\n" +
                "0 activation,\n" +
                "0 cumul_activation,  \n" +
                "0 cb_30jours,\n" +
                "0 cb_7jours, \n" +
                "0 cb_30jours_data,\n" +
                "0 mtt_rec,\n" +
                "SUM(amnt) cumul_mtt_rec\n" +
                "FROM\t\n" +
                "(\n" +
                "SELECT \n" +
                "DATE_FORMAT(upd_dt, '%m-%Y') periode,\n" +
                "MAX(upd_dt) upd_dt, \n" +
                "IFNULL(sg.sig_zoneorange_name_v3,'INCONNU') zone, \n" +
                "SUM(amount_htva) amnt\n" +
                "FROM `DM_CB`.`cb_recharge_qs` qs\n" +
                "LEFT JOIN DM_RF.rf_sig_cell_krill_v3 sg ON sg.sig_cel=qs.cel AND sg.sig_lac=qs.lac\n" +
                "WHERE upd_dt BETWEEN  '" + start + "' AND '" + end + "' AND billing_type = 1\n" +
                "GROUP BY periode, sg.sig_zoneorange_name_v3 \n" +
                ")cumul_rec\n" +
                "GROUP BY zone\n" +
                "\n" +
                "UNION ALL\n" +
                "\n" +
                "SELECT\n" +
                "DATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "upd_dt,\n" +
                "IF(rf.sig_zoneorange_name_v3 IS NULL,'INCONNU',rf.sig_zoneorange_name_v3) zone,\n" +
                "0 parc,\n" +
                "0 delta_parc,\n" +
                "0 activation,\n" +
                "0 cumul_activation,  \n" +
                "SUM(qty2) cb_30jours,\n" +
                "SUM(qty4) cb_7jours, \n" +
                "SUM(qty3) cb_30jours_data,\n" +
                "0 mtt_rec,\n" +
                "0 cumul_mtt_rec\n" +
                "FROM DM_OD.od_parc_orange od \n" +
                "LEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_lac_ci\n" +
                "WHERE upd_dt = '" + end + "'\n" +
                "AND statut <> 4\n" +
                "AND billing_type = 1\n" +
                "GROUP BY \n" +
                "upd_dt, zone\n" +
                ") table_finale\n" +
                "GROUP BY jour,zone;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            List<DwhRes> dwhResList = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    DwhRes dwhRes = new DwhRes();
                    dwhRes.setMois_annee(rs.getString("mois_annee"));
                    dwhRes.setJour(LocalDate.parse(rs.getString("jour")));
                    dwhRes.setZone(rs.getString("zone"));
                    dwhRes.setParc(rs.getLong("parc"));
                    dwhRes.setDelta_parc(rs.getLong("delta_parc"));
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
