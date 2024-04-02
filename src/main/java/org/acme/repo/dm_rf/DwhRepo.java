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
                "\tSUM(parc) 'Parc_journalier_Telco+OM',\n" +
                "\tSUM(activation) Activation_journalier,\n" +
                "\tSUM(cumul_activation) Cumul_Activation,  \n" +
                "\tSUM(cb_30jours) cb_30jours,\n" +
                "\tSUM(cb_7jours) cb_7jours, \n" +
                "\tSUM(cb_30jours_data) cb_30jours_data,\n" +
                "\t#SUM(nb_rec) Nombre_Rechargement,\n" +
                "\t#SUM(cumul_nb_rec) Cumul_nb_Rechargement,\n" +
                "\tSUM(mtt_rec) Montant_Rechargement,\n" +
                "\tSUM(cumul_mtt_rec) Cumul_Montant_Rechargement\n" +
                "FROM (\n" +
                "\t###### PARC ######\n" +
                "\t#parc_journalier\n" +
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
                "\t\t#0 nb_rec,\n" +
                "\t\t#0 cumul_nb_rec,\n" +
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
                "\t###### ACTIVATION ######\n" +
                "\t#activation_journalier\n" +
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
                "\t\t#0 nb_rec,\n" +
                "\t\t#0 cumul_nb_rec,\n" +
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
                "\t#Cumul_activation\n" +
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
                "\t\t#0 nb_rec,\n" +
                "\t\t#0 cumul_nb_rec,\n" +
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
                "\t###### RECHARGEMENT ######\n" +
                "\t#Rechargement journalier\n" +
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
                "\t\t#(rec.qty_scratch+(rec.qty_erecharge+rec.qty_om)) nb_rec,\n" +
                "\t\t#0 cumul_nb_rec,\n" +
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
                "\t#Cumul Rechargement\n" +
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
                "\t\t#0 nb_rec,\n" +
                "\t\t#(cumul_rec.qty_scratch+(cumul_rec.qty_erecharge+cumul_rec.qty_om)) cumul_nb_rec,\n" +
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
                "\t#Charged Base\n" +
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
                "\t\t#0 nb_rec,\n" +
                "\t\t#0 cumul_nb_rec,\n" +
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
        String query = "SELECT " +
                "mois_annee," +
                "upd_dt jour," +
                "zone," +
                "SUM(parc) 'parc'," +
                "SUM(activation) activation," +
                "SUM(cumul_activation) cumul_activation," +
                "SUM(cb_30jours) cb_30jours,"+
                "SUM(cb_7jours) cb_7jours," +
                "SUM(cb_30jours_data) cb_30jours_data," +
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
                "upd_dt," +
                "CASE " +
                "WHEN rf.sig_zoneorange_name_v3 IS NULL THEN 'INCONNU'" +
                "ELSE rf.sig_zoneorange_name_v3 " +
                "END AS zone," +
                "0 parc," +
                "0 activation," +
                "0 cumul_activation," +
                "SUM(qty2) cb_30jours," +
                "SUM(qty4) cb_7jours," +
                "SUM(qty3) cb_30jours_data," +
                "0 nb_rec," +
                "0 cumul_nb_rec," +
                "0 mtt_rec," +
                "0 cumul_mtt_rec " +
                "FROM DM_OD.od_parc_orange od " +
                "LEFT JOIN DM_RF.`rf_sig_cell_krill_v3` rf ON od.site_id =rf.sig_id " +
                "WHERE upd_dt = '" + endDate + "' " +
                "AND statut <> '4' " +
                "AND billing_type = '1' " +
                "GROUP BY upd_dt, zone " +
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
