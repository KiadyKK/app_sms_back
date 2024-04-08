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
        String query = "SELECT \n" +
                "mois_annee, \n" +
                "upd_dt jour, \n" +
                "zone, \n" +
                "SUM(parc) 'parc', \n" +
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
                "CASE\n" +
                "WHEN rf.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'\n" +
                "ELSE rf.sig_zoneorange_name_v3 \n" +
                "END AS zone,\n" +
                "SUM(qty) parc,\n" +
                "0 activation,\n" +
                "0 cumul_activation,  \n" +
                "0 cb_30jours,\n" +
                "0 cb_7jours, \n" +
                "0 cb_30jours_data,\n" +
                "0 mtt_rec,\n" +
                "0 cumul_mtt_rec\n" +
                "FROM  DM_OD.od_parc_orange od \n" +
                "LEFT JOIN DM_RF.rf_customer_group cg ON od.prg_code=cg.prgcode AND cg.type ='Facturable'\n" +
                "LEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_id\n" +
                "WHERE upd_dt = '" + endDate + "'\n" +
                "AND statut IN (1,2,5)\n" +
                "AND parc_id = 1 \n" +
                "AND billing_type IN(1,5)\n" +
                "GROUP BY upd_dt, zone\n" +
                "\n" +
                "UNION ALL\n" +
                "\n" +
                "SELECT  \n" +
                "DATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "upd_dt,\n" +
                "CASE\n" +
                "WHEN rf.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'\n" +
                "ELSE rf.sig_zoneorange_name_v3 \n" +
                "END AS zone,\n" +
                "0 parc,\n" +
                "SUM(IF(parc_id=1,qty,0)) activation,\n" +
                "0 cumul_activation,  \n" +
                "0 cb_30jours,\n" +
                "0 cb_7jours, \n" +
                "0 cb_30jours_data,\n" +
                "0 mtt_rec,\n" +
                "0 cumul_mtt_rec\n" +
                "FROM  DM_OD.od_parc_orange od \n" +
                "LEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_id\n" +
                "WHERE upd_dt = '" + endDate + "'\n" +
                "AND billing_type IN (1,5)\n" +
                "AND statut = 1\n" +
                "GROUP BY upd_dt, zone\n" +
                "\n" +
                "UNION ALL\n" +
                "\n" +
                "SELECT  \n" +
                "DATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "MAX(upd_dt),\n" +
                "CASE\n" +
                "WHEN rf.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'\n" +
                "ELSE rf.sig_zoneorange_name_v3 \n" +
                "END AS zone,\n" +
                "0 parc,\n" +
                "0 activation,\n" +
                "SUM(IF(parc_id=1,qty,0)) cumul_activation,  \n" +
                "0 cb_30jours,\n" +
                "0 cb_7jours, \n" +
                "0 cb_30jours_data,\n" +
                "0 mtt_rec,\n" +
                "0 cumul_mtt_rec\n" +
                "FROM DM_OD.od_parc_orange od \n" +
                "LEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_id\n" +
                "WHERE upd_dt BETWEEN '" + startDate + "' AND '" + endDate + "'\n" +
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
                "0 activation,\n" +
                "0 cumul_activation,  \n" +
                "0 cb_30jours,\n" +
                "0 cb_7jours, \n" +
                "0 cb_30jours_data,\n" +
                "(rec.achat_scratch+(rec.achat_erecharge+rec.achat_om)) mtt_rec,\n" +
                "0 cumul_mtt_rec\n" +
                "FROM (\n" +
                "SELECT \n" +
                "upd_dt,\n" +
                "SUM(IF(rf.id IN (1,3), qty, 0)) qty_scratch,\n" +
                "SUM(IF(rf.id IN (1,3), (amount*qty)/1.2, 0)) achat_scratch,\n" +
                "SUM(IF(rf.id IN(2,5,6,10,11), qty, 0)) qty_erecharge, \n" +
                "SUM(IF(rf.id IN (2,5,6,10,11),(amount*qty)/1.2, 0)) achat_erecharge,\n" +
                "SUM(IF(rf.id = 4,qty, 0)) qty_om,\n" +
                "SUM(IF(rf.id = 4, (amount*qty)/1.2, 0)) achat_om,\n" +
                "SUM(fees) fees,\n" +
                "CASE \n" +
                "WHEN sg.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'\n" +
                "ELSE sg.sig_zoneorange_name_v3 \n" +
                "END AS zone\n" +
                "FROM DM_OD.od_recharge c\n" +
                "INNER JOIN DM_RF.`rf_recharge_code` rc ON rc.code = c.channel_id\n" +
                "INNER JOIN DM_RF.rf_rec_type rf  ON rf.id = rc.rec_type\n" +
                "LEFT JOIN DM_RF.rf_sig_cell_krill_v3 sg ON sg.sig_lac_ci=c.cell_id\n" +
                "WHERE upd_dt = '" + endDate + "' GROUP BY upd_dt, zone\n" +
                ") rec\n" +
                "\t\n" +
                "UNION ALL\n" +
                "\n" +
                "SELECT \n" +
                "cumul_rec.periode mois_annee,\n" +
                "cumul_rec.upd_dt upd_dt,\n" +
                "cumul_rec.zone zone,\n" +
                "0 parc,\n" +
                "0 activation,\n" +
                "0 cumul_activation,  \n" +
                "0 cb_30jours,\n" +
                "0 cb_7jours, \n" +
                "0 cb_30jours_data,\n" +
                "0 mtt_rec,\n" +
                "(cumul_rec.achat_scratch+(cumul_rec.achat_erecharge+cumul_rec.achat_om)) cumul_mtt_rec\n" +
                "FROM (\n" +
                "SELECT\n" +
                "DATE_FORMAT(upd_dt, '%m-%Y') periode,\n" +
                "MAX(upd_dt) upd_dt,\n" +
                "SUM(IF(rf.id IN (1,3), qty, 0)) qty_scratch,\n" +
                "SUM(IF(rf.id IN (1,3), (amount*qty)/1.2, 0)) achat_scratch,\n" +
                "SUM(IF(rf.id IN(2,5,6,10,11), qty, 0)) qty_erecharge, \n" +
                "SUM(IF(rf.id IN (2,5,6,10,11),(amount*qty)/1.2, 0)) achat_erecharge,\n" +
                "SUM(IF(rf.id = 4,qty, 0)) qty_om,\n" +
                "SUM(IF(rf.id = 4, (amount*qty)/1.2, 0)) achat_om,\n" +
                "SUM(fees) fees,\n" +
                "CASE \n" +
                "WHEN sg.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'\n" +
                "ELSE sg.sig_zoneorange_name_v3 \n" +
                "END AS zone\n" +
                "FROM DM_OD.od_recharge c \n" +
                "INNER JOIN DM_RF.`rf_recharge_code` rc ON rc.code = c.channel_id\n" +
                "INNER JOIN DM_RF.rf_rec_type rf  ON rf.id = rc.rec_type\n" +
                "LEFT JOIN DM_RF.rf_sig_cell_krill_v3 sg ON sg.sig_lac_ci=c.cell_id\n" +
                "WHERE upd_dt BETWEEN '" + startDate + "' AND '" + endDate + "'\n" +
                "GROUP BY zone \n" +
                ") cumul_rec\n" +
                "\n" +
                "UNION ALL\n" +
                "SELECT\n" +
                "DATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "upd_dt,\n" +
                "CASE\n" +
                "WHEN rf.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'\n" +
                "ELSE rf.sig_zoneorange_name_v3 \n" +
                "END AS zone,\n" +
                "0 parc,\n" +
                "0 activation,\n" +
                "0 cumul_activation,  \n" +
                "SUM(qty2) cb_30jours,\n" +
                "SUM(qty4) cb_7jours, \n" +
                "SUM(qty3) cb_30jours_data,\n" +
                "0 mtt_rec,\n" +
                "0 cumul_mtt_rec\n" +
                "FROM DM_OD.od_parc_orange od \n" +
                "LEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_id\n" +
                "WHERE upd_dt = '" + endDate + "'\n" +
                "AND statut <> '4'\n" +
                "AND billing_type = '1'\n" +
                "GROUP BY \n" +
                "upd_dt, zone) table_finale\n" +
                "GROUP BY jour, zone";

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
