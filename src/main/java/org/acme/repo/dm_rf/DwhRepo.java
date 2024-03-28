package org.acme.repo.dm_rf;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.dm_rf.DwhRes;
import org.acme.model.dm_rf.Zone;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DwhRepo {
    @Inject
    @DataSource("dm_rf")
    AgroalDataSource dataSource;

    public List<DwhRes> getAll(LocalDate startDate, LocalDate endDate) {
        String query = "SELECT " +
                "mois_annee," +
                "upd_dt jour," +
                "zone," +
                "SUM(parc) 'parc'," +
                "SUM(activation) activation," +
                "SUM(cumul_activation) cumul_activation," +
                "SUM(nb_rec) nb_rec," +
                "SUM(cumul_nb_rec) cumul_nb_rec," +
                "SUM(mtt_rec) mtt_rec," +
                "SUM(cumul_mtt_rec) cumul_mtt_rec " +
                "FROM (" +
                "SELECT " +
                "DATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee," +
                "upd_dt," +
                "CASE " +
                "WHEN rf.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU' " +
                "ELSE rf.sig_zoneorange_name_v3 " +
                "END AS zone," +
                "SUM(qty) parc," +
                "0 activation," +
                "0 cumul_activation," +
                "0 nb_rec," +
                "0 cumul_nb_rec," +
                "0 mtt_rec," +
                "0 cumul_mtt_rec " +
                "FROM  DM_OD.od_parc_orange od " +
                "LEFT JOIN DM_RF.rf_customer_group cg ON od.prg_code=cg.prgcode AND cg.type ='Facturable' " +
                "LEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_id " +
                "WHERE upd_dt = '" + endDate + "' " +
                "AND statut IN (1,2,5) " +
                "AND parc_id = 1 " +
                "AND billing_type IN(1,5) " +
                "GROUP BY upd_dt, zone " +
                "UNION ALL " +
                "SELECT " +
                "DATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee," +
                "upd_dt," +
                "CASE " +
                "WHEN rf.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU' " +
                "ELSE rf.sig_zoneorange_name_v3 " +
                "END AS zone," +
                "0 parc," +
                "SUM(IF(parc_id=1,qty,0)) activation," +
                "0 cumul_activation," +
                "0 nb_rec," +
                "0 cumul_nb_rec," +
                "0 mtt_rec," +
                "0 cumul_mtt_rec " +
                "FROM  DM_OD.od_parc_orange od " +
                "LEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id = rf.sig_id " +
                "WHERE upd_dt = '" + endDate + "' " +
                "AND billing_type IN (1,5) " +
                "AND statut = 1 " +
                "GROUP BY upd_dt, zone " +
                "UNION ALL " +
                "SELECT " +
                "DATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee," +
                "MAX(upd_dt)," +
                "CASE " +
                "WHEN rf.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU' " +
                "ELSE rf.sig_zoneorange_name_v3 " +
                "END AS zone," +
                "0 parc," +
                "0 activation," +
                "SUM(IF(parc_id=1,qty,0)) cumul_activation," +
                "0 nb_rec," +
                "0 cumul_nb_rec," +
                "0 mtt_rec," +
                "0 cumul_mtt_rec " +
                "FROM  DM_OD.od_parc_orange od " +
                "LEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id = rf.sig_id " +
                "WHERE upd_dt BETWEEN '" + startDate + "' AND '" + endDate + "' " +
                "AND billing_type IN (1,5) " +
                "AND statut = 1 " +
                "GROUP BY mois_annee, zone " +
                "UNION ALL " +
                "SELECT " +
                "DATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee," +
                "rec.upd_dt upd_dt," +
                "rec.zone zone," +
                "0 parc," +
                "0 activation," +
                "0 cumul_activation," +
                "(rec.qty_scratch+(rec.qty_erecharge+rec.qty_om)) nb_rec," +
                "0 cumul_nb_rec," +
                "(rec.achat_scratch+(rec.achat_erecharge+rec.achat_om)) mtt_rec," +
                "0 cumul_mtt_rec " +
                "FROM " +
                "(" +
                "SELECT " +
                "upd_dt," +
                "SUM(IF(rf.id IN (1,3), qty, 0)) qty_scratch," +
                "SUM(IF(rf.id IN (1,3), (amount*qty)/1.2, 0)) achat_scratch," +
                "SUM(IF(rf.id IN(2,5,6,10,11), qty, 0)) qty_erecharge," +
                "SUM(IF(rf.id IN (2,5,6,10,11),(amount*qty)/1.2, 0)) achat_erecharge," +
                "SUM(IF(rf.id = 4,qty, 0)) qty_om," +
                "SUM(IF(rf.id = 4, (amount*qty)/1.2, 0)) achat_om," +
                "SUM(fees) fees," +
                "CASE " +
                "WHEN sg.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU' " +
                "ELSE sg.sig_zoneorange_name_v3 " +
                "END AS zone " +
                "FROM DM_OD.od_recharge c " +
                "INNER JOIN DM_RF.`rf_recharge_code` rc ON rc.code = c.channel_id " +
                "INNER JOIN DM_RF.rf_rec_type rf  ON rf.id = rc.rec_type " +
                "LEFT JOIN DM_RF.rf_sig_cell_krill_v3 sg ON sg.sig_lac_ci=c.cell_id " +
                "WHERE upd_dt = '" + endDate + "' GROUP BY upd_dt, zone " +
                ")rec " +
                "UNION ALL " +
                "SELECT " +
                "cumul_rec.periode mois_annee," +
                "cumul_rec.upd_dt upd_dt," +
                "cumul_rec.zone zone," +
                "0 parc," +
                "0 activation," +
                "0 cumul_activation," +
                "0 nb_rec," +
                "(cumul_rec.qty_scratch+(cumul_rec.qty_erecharge+cumul_rec.qty_om)) cumul_nb_rec," +
                "0 mtt_rec," +
                "(cumul_rec.achat_scratch+(cumul_rec.achat_erecharge+cumul_rec.achat_om)) cumul_mtt_rec " +
                "FROM " +
                "(" +
                "SELECT " +
                "DATE_FORMAT(upd_dt, '%m-%Y') periode," +
                "MAX(upd_dt) upd_dt," +
                "SUM(IF(rf.id IN (1,3), qty, 0)) qty_scratch," +
                "SUM(IF(rf.id IN (1,3), (amount*qty)/1.2, 0)) achat_scratch," +
                "SUM(IF(rf.id IN(2,5,6,10,11), qty, 0)) qty_erecharge, " +
                "SUM(IF(rf.id IN (2,5,6,10,11),(amount*qty)/1.2, 0)) achat_erecharge," +
                "SUM(IF(rf.id = 4,qty, 0)) qty_om," +
                "SUM(IF(rf.id = 4, (amount*qty)/1.2, 0)) achat_om," +
                "SUM(fees) fees," +
                "CASE " +
                "WHEN sg.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU' " +
                "ELSE sg.sig_zoneorange_name_v3 " +
                "END AS zone " +
                "FROM DM_OD.od_recharge c " +
                "INNER JOIN DM_RF.`rf_recharge_code` rc ON rc.code = c.channel_id " +
                "INNER JOIN DM_RF.rf_rec_type rf  ON rf.id = rc.rec_type " +
                "LEFT JOIN DM_RF.rf_sig_cell_krill_v3 sg ON sg.sig_lac_ci=c.cell_id " +
//                "WHERE upd_dt BETWEEN '" + startDate + "' AND '" + endDate + "' " +
                "WHERE upd_dt BETWEEN '" + startDate + "' AND '" + endDate + "' " +
                "GROUP BY zone " +
                ")cumul_rec " +
                ") table_finale " +
                "GROUP BY jour, zone";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            List<DwhRes> dwhResList = new ArrayList<>();
            try(  ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    DwhRes dwhRes = new DwhRes();
                    dwhRes.setMois_annee(rs.getString("mois_annee"));
                    dwhRes.setJour(LocalDate.parse(rs.getString("jour")));
                    dwhRes.setZone(rs.getString("zone"));
                    dwhRes.setParc(rs.getLong("parc"));
                    dwhRes.setActivation(rs.getLong("activation"));
                    dwhRes.setCumul_activation(rs.getLong("cumul_activation"));
                    dwhRes.setNb_rec(rs.getLong("nb_rec"));
                    dwhRes.setCumul_nb_rec(rs.getLong("cumul_nb_rec"));
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

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            List<Zone> zoneList = new ArrayList<>();
            try(  ResultSet rs = stmt.executeQuery(query)) {
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
