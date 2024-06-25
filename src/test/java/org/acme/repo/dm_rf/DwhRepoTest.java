package org.acme.repo.dm_rf;
import io.agroal.api.AgroalDataSource;
import io.quarkus.test.Mock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.model.dm_rf.DwhRes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class DwhRepoTest {
    /*
    @Inject
    DwhRepo dwhRepo;
    @TestTransaction
    @Test
    void getAll() throws SQLException {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        AgroalDataSource dataSource= Mockito.mock(AgroalDataSource.class);
        Connection connection=Mockito.mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);

        ResultSet resultSet=new ResultSet() {

            @Override
            public String getString(int columnIndex) throws SQLException {
                return "";
            }

            @Override
            public Date getDate(int columnIndex) throws SQLException {
                return null;
            }
        };

        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("mois_annee")).thenReturn("01-2024");
        when(resultSet.getString("jour")).thenReturn("2024-01-31");
        when(resultSet.getString("zone")).thenReturn("Zone1");
        when(resultSet.getLong("parc")).thenReturn(100L);
        when(resultSet.getLong("activation")).thenReturn(50L);
        when(resultSet.getLong("cumul_activation")).thenReturn(200L);
        when(resultSet.getLong("cb_30j")).thenReturn(300L);
        when(resultSet.getLong("cb_7j")).thenReturn(400L);
        when(resultSet.getLong("cb_30jd")).thenReturn(500L);
        when(resultSet.getDouble("mtt_rec")).thenReturn(1000.0);
        when(resultSet.getDouble("cumul_mtt_rec")).thenReturn(2000.0);


        PreparedStatement preparedStatement=Mockito.mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);


        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        // When
        List<DwhRes> result = dwhRepo.getAll(startDate, endDate);

        // Then
        assertEquals(1, result.size());
        DwhRes dwhRes = result.get(0);
        assertEquals("01-2024", dwhRes.getMois_annee());
        assertEquals(LocalDate.parse("2024-01-31"), dwhRes.getJour());
        assertEquals("Zone1", dwhRes.getZone());
        assertEquals(100L, dwhRes.getParc());
        assertEquals(50L, dwhRes.getActivation());
        assertEquals(200L, dwhRes.getCumul_activation());
        assertEquals(300L, dwhRes.getCb_30j());
        assertEquals(400L, dwhRes.getCb_7j());
        assertEquals(500L, dwhRes.getCb_30jd());
        assertEquals(1000.0, dwhRes.getMtt_rec());
        assertEquals(2000.0, dwhRes.getCumul_mtt_rec());

        // Verify interactions
       Mockito.verify(dataSource).getConnection();
        Mockito.verify(connection).prepareStatement(any(String.class));
        Mockito.verify(preparedStatement).executeQuery();
        Mockito.verify(resultSet).next();


    }
    @TestTransaction
    @Test
    void getAllZone() {
    }

     */
}