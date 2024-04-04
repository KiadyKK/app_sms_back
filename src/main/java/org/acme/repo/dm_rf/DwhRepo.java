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
        String query1="SELECT\n" +
                "\tmois_annee,\n" +
                "\tupd_dt jour,\n" +
                "\tzone,\n" +
                "\t-- users.user_phone USER_PHONE,\n" +
                "\tSUM(parc) 'parc',\n" +
                "\tSUM(activation) activation,\n" +
                "\tSUM(cumul_activation) cumul_activation,  \n" +
                "\tSUM(cb_30jours) cb_30jours,\n" +
                "\tSUM(cb_7jours) cb_7jours, \n" +
                "\tSUM(cb_30jours_data) cb_30jours_data,\n" +
                "\tSUM(nb_rec) nb_rec,\n" +
                "\tSUM(cumul_nb_rec) cumul_nb_rec,\n" +
                "\tSUM(mtt_rec) mtt_rec,\n" +
                "\tSUM(cumul_mtt_rec) cumul_mtt_rec\n" +
                "FROM (\n" +
                "\tSELECT  \n" +
                "\t\tDATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "\t\tupd_dt,\n" +
                "\t\tCASE\n" +
                "\t\tWHEN rf.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'\n" +
                "\t\tELSE rf.sig_zoneorange_name_v3 \n" +
                "\t\tEND AS zone,\n" +
                "\t\tSUM(qty) parc,\n" +
                "\t\t0 activation,\n" +
                "\t\t0 cumul_activation,  \n" +
                "\t\t0 cb_30jours,\n" +
                "\t\t0 cb_7jours, \n" +
                "\t\t0 cb_30jours_data,\n" +
                "\t\t0 nb_rec,\n" +
                "\t\t0 cumul_nb_rec,\n" +
                "\t\t0 mtt_rec,\n" +
                "\t\t0 cumul_mtt_rec\n" +
                "\tFROM  DM_OD.od_parc_orange od \n" +
                "\tLEFT JOIN DM_RF.rf_customer_group cg ON od.prg_code=cg.prgcode AND cg.type ='Facturable'\n" +
                "\tLEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_id\n" +
                "\tWHERE upd_dt = '"+endDate+"'\n" +
                "\tAND statut IN (1,2,5)\n" +
                "\tAND parc_id = 1 \n" +
                "\tAND billing_type IN(1,5)\n" +
                "\tGROUP BY upd_dt, zone\n" +
                "\t\n" +
                "\tUNION ALL\n" +
                "\t\n" +
                "\tSELECT  \n" +
                "\t\tDATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "\t\tupd_dt,\n" +
                "\t\tCASE\n" +
                "\t\tWHEN rf.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'\n" +
                "\t\tELSE rf.sig_zoneorange_name_v3 \n" +
                "\t\tEND AS zone,\n" +
                "\t\t0 parc,\n" +
                "\t\tSUM(IF(parc_id=1,qty,0)) activation,\n" +
                "\t\t0 cumul_activation,  \n" +
                "\t\t0 cb_30jours,\n" +
                "\t\t0 cb_7jours, \n" +
                "\t\t0 cb_30jours_data,\n" +
                "\t\t0 nb_rec,\n" +
                "\t\t0 cumul_nb_rec,\n" +
                "\t\t0 mtt_rec,\n" +
                "\t\t0 cumul_mtt_rec\n" +
                "\tFROM  DM_OD.od_parc_orange od \n" +
                "\tLEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_id\n" +
                "\tWHERE upd_dt = '"+endDate+"'\n" +
                "\tAND billing_type IN (1,5)\n" +
                "\tAND statut = 1\n" +
                "\tGROUP BY upd_dt, zone\n" +
                "\t\n" +
                "\tUNION ALL\n" +
                "\t\n" +
                "\tSELECT  \n" +
                "\t\tDATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "\t\tMAX(upd_dt),\n" +
                "\t\tCASE\n" +
                "\t\tWHEN rf.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'\n" +
                "\t\tELSE rf.sig_zoneorange_name_v3 \n" +
                "\t\tEND AS zone,\n" +
                "\t\t0 parc,\n" +
                "\t\t0 activation,\n" +
                "\t\tSUM(IF(parc_id=1,qty,0)) cumul_activation,  \n" +
                "\t\t0 cb_30jours,\n" +
                "\t\t0 cb_7jours, \n" +
                "\t\t0 cb_30jours_data,\n" +
                "\t\t0 nb_rec,\n" +
                "\t\t0 cumul_nb_rec,\n" +
                "\t\t0 mtt_rec,\n" +
                "\t\t0 cumul_mtt_rec\n" +
                "\tFROM  DM_OD.od_parc_orange od \n" +
                "\tLEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_id\n" +
                "\tWHERE upd_dt BETWEEN '"+startDate+"' AND '"+endDate+"'\n" +
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
                "\t\t0 activation,\n" +
                "\t\t0 cumul_activation,  \n" +
                "\t\t0 cb_30jours,\n" +
                "\t\t0 cb_7jours, \n" +
                "\t\t0 cb_30jours_data,\n" +
                "\t\t(rec.qty_scratch+(rec.qty_erecharge+rec.qty_om)) nb_rec,\n" +
                "\t\t0 cumul_nb_rec,\n" +
                "\t\t(rec.achat_scratch+(rec.achat_erecharge+rec.achat_om)) mtt_rec,\n" +
                "\t\t0 cumul_mtt_rec\n" +
                "\tFROM\t\n" +
                "\t\t(\n" +
                "\t\tSELECT \n" +
                "\t\t\tupd_dt,\n" +
                "\t\t\tSUM(IF(rf.id IN (1,3), qty, 0)) qty_scratch,\n" +
                "\t\t\tSUM(IF(rf.id IN (1,3), (amount*qty)/1.2, 0)) achat_scratch,\n" +
                "\t\t\tSUM(IF(rf.id IN(2,5,6,10,11), qty, 0)) qty_erecharge, \n" +
                "\t\t\tSUM(IF(rf.id IN (2,5,6,10,11),(amount*qty)/1.2, 0)) achat_erecharge,\n" +
                "\t\t\tSUM(IF(rf.id = 4,qty, 0)) qty_om,\n" +
                "\t\t\tSUM(IF(rf.id = 4, (amount*qty)/1.2, 0)) achat_om,\n" +
                "\t\t\tSUM(fees) fees,\n" +
                "\t\t\tCASE \n" +
                "\t\t\tWHEN sg.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'\n" +
                "\t\t\tELSE sg.sig_zoneorange_name_v3 \n" +
                "\t\t\tEND AS zone\n" +
                "\t\tFROM DM_OD.od_recharge c\n" +
                "\t\t\tINNER JOIN DM_RF.`rf_recharge_code` rc ON rc.code = c.channel_id\n" +
                "\t\t\tINNER JOIN DM_RF.rf_rec_type rf  ON rf.id = rc.rec_type\n" +
                "\t\t\tLEFT JOIN DM_RF.rf_sig_cell_krill_v3 sg ON sg.sig_lac_ci=c.cell_id\n" +
                "\t\tWHERE upd_dt = '"+endDate+"' GROUP BY upd_dt, zone\n" +
                "\t\t)rec\n" +
                "\t\n" +
                "\tUNION ALL\n" +
                "\n" +
                "\tSELECT \n" +
                "\t\tcumul_rec.periode mois_annee,\n" +
                "\t\tcumul_rec.upd_dt upd_dt,\n" +
                "\t\tcumul_rec.zone zone,\n" +
                "\t\t0 parc,\n" +
                "\t\t0 activation,\n" +
                "\t\t0 cumul_activation,  \n" +
                "\t\t0 cb_30jours,\n" +
                "\t\t0 cb_7jours, \n" +
                "\t\t0 cb_30jours_data,\n" +
                "\t\t0 nb_rec,\n" +
                "\t\t(cumul_rec.qty_scratch+(cumul_rec.qty_erecharge+cumul_rec.qty_om)) cumul_nb_rec,\n" +
                "\t\t0 mtt_rec,\n" +
                "\t\t(cumul_rec.achat_scratch+(cumul_rec.achat_erecharge+cumul_rec.achat_om)) cumul_mtt_rec\n" +
                "\tFROM\t\n" +
                "\t\t(\n" +
                "\t\tSELECT\n" +
                "\t\t\tDATE_FORMAT(upd_dt, '%m-%Y') periode,\n" +
                "\t\t\tMAX(upd_dt) upd_dt,\n" +
                "\t\t\tSUM(IF(rf.id IN (1,3), qty, 0)) qty_scratch,\n" +
                "\t\t\tSUM(IF(rf.id IN (1,3), (amount*qty)/1.2, 0)) achat_scratch,\n" +
                "\t\t\tSUM(IF(rf.id IN(2,5,6,10,11), qty, 0)) qty_erecharge, \n" +
                "\t\t\tSUM(IF(rf.id IN (2,5,6,10,11),(amount*qty)/1.2, 0)) achat_erecharge,\n" +
                "\t\t\tSUM(IF(rf.id = 4,qty, 0)) qty_om,\n" +
                "\t\t\tSUM(IF(rf.id = 4, (amount*qty)/1.2, 0)) achat_om,\n" +
                "\t\t\tSUM(fees) fees,\n" +
                "\t\t\tCASE \n" +
                "\t\t\tWHEN sg.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'\n" +
                "\t\t\tELSE sg.sig_zoneorange_name_v3 \n" +
                "\t\t\tEND AS zone\n" +
                "\t\tFROM DM_OD.od_recharge c \n" +
                "\t\t\tINNER JOIN DM_RF.`rf_recharge_code` rc ON rc.code = c.channel_id\n" +
                "\t\t\tINNER JOIN DM_RF.rf_rec_type rf  ON rf.id = rc.rec_type\n" +
                "\t\t\tLEFT JOIN DM_RF.rf_sig_cell_krill_v3 sg ON sg.sig_lac_ci=c.cell_id\n" +
                "\t\tWHERE upd_dt BETWEEN '"+startDate+"' AND '"+endDate+"'\n" +
                "\t\tGROUP BY zone \n" +
                "\t\t)cumul_rec\n" +
                "\t\t\n" +
                "\t\tUNION ALL\n" +
                "\t\t\n" +
                "\tSELECT\n" +
                "\t\tDATE_FORMAT(upd_dt, \"%m-%Y\") mois_annee,\n" +
                "\t\tupd_dt,\n" +
                "\t\tCASE\n" +
                "\t\tWHEN rf.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'\n" +
                "\t\tELSE rf.sig_zoneorange_name_v3 \n" +
                "\t\tEND AS zone,\n" +
                "\t\t0 parc,\n" +
                "\t\t0 activation,\n" +
                "\t\t0 cumul_activation,  \n" +
                "\t\tSUM(qty2) cb_30jours,\n" +
                "\t\tSUM(qty4) cb_7jours, \n" +
                "\t\tSUM(qty3) cb_30jours_data,\n" +
                "\t\t0 nb_rec,\n" +
                "\t\t0 cumul_nb_rec,\n" +
                "\t\t0 mtt_rec,\n" +
                "\t\t0 cumul_mtt_rec\n" +
                "\tFROM DM_OD.od_parc_orange od \n" +
                "\tLEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_id\n" +
                "\tWHERE upd_dt = \"'"+ endDate+"'\"\n" +
                "\t\tAND statut <> '4'\n" +
                "\t\tAND billing_type = '1'\n" +
                "\tGROUP BY \n" +
                "\t\tupd_dt, zone\n" +
                "\n" +
                ") table_finale\n" +
                "/*,\n" +
                "(\n" +
                "\tSELECT * FROM DM_CB.cb_user_sms\n" +
                "\tWHERE user_id= 7\n" +
                ")users*/\n" +
                "GROUP BY jour, /*users.user_phone,*/ zone \n" +
                ";";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query1)) {
            List<DwhRes> dwhResList = new ArrayList<>();
            try(  ResultSet rs = stmt.executeQuery(query1)) {
                while (rs.next()) {
                    DwhRes dwhRes = new DwhRes();
                    dwhRes.setMois_annee(rs.getString("mois_annee"));
                    dwhRes.setJour(LocalDate.parse(rs.getString("jour")));
                    dwhRes.setZone(rs.getString("zone"));
                    dwhRes.setParc(rs.getLong("parc"));
                    dwhRes.setActivation(rs.getLong("activation"));
                    dwhRes.setCumul_activation(rs.getLong("cumul_activation"));
                    dwhRes.setCb_30jours(rs.getLong("cb_30jours"));
                    dwhRes.setCb_7jours(rs.getLong("cb_7jours"));
                    dwhRes.setCb_30jours_data(rs.getLong("cb_30jours_data"));
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
