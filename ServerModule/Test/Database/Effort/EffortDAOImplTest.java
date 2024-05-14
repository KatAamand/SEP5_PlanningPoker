package Database.Effort;

import DataTypes.Effort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class EffortDAOImplTest {

    private EffortDAOImpl effortDAO;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        try {
            effortDAO = EffortDAOImpl.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        mockConnection = Mockito.mock(Connection.class);
        mockPreparedStatement = Mockito.mock(PreparedStatement.class);
        mockResultSet = Mockito.mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test // Looking for an empty string in the efforts table, should return null
    public void testReadByEffort_zero() throws SQLException {
        // arrange
        when(mockResultSet.next()).thenReturn(false);

        // act
        Effort effort = effortDAO.readByEffort("");

        // assert
        assertNull(effort);
    }

    @Test // Looking for a known item in the efforts tabel: "2", should return Effort object with value "2" and imagepath "/Images/2.png"
    public void testReadByEffort_one() throws SQLException {
        // arrange
        String effortValue = "2";
        String imagePath = "/Images/2.png";
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("value")).thenReturn(effortValue);
        when(mockResultSet.getString("imagepath")).thenReturn(imagePath);

        // act
        Effort effort = effortDAO.readByEffort(effortValue);

        // assert
        assertNotNull(effort);
        assertEquals(effortValue, effort.getEffortValue());
        assertEquals(imagePath, effort.getImgPath());
    }

    @Test // Looking for a null item in the efforts table, should return null
    public void testGetEffortList_zero() throws SQLException {
        // arrange
        when(mockResultSet.next()).thenReturn(false);

        // act
        var effortList = effortDAO.getEffortList();

        // assert
        assertNotNull(effortList);
        assertEquals(0, effortList.size());
    }

    @Test // Looking for all 13 items in the efforts table
    public void testGetEffortList_many() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);

        ArrayList<Effort> efforts = effortDAO.getEffortList();
        assertEquals(13, efforts.size());
    }

    @Test // Testing if the Effort objects are created correctly
    // Here we are looking at the first item, the 7th item and the last item, the boundaries of the table
    public void testGetEffortList_ObjectsInTable() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);

        ArrayList<Effort> efforts = effortDAO.getEffortList();
        assertEquals("0", efforts.get(0).getEffortValue());
        assertEquals("/Images/0.png", efforts.get(0).getImgPath());
        assertEquals("13", efforts.get(7).getEffortValue());
        assertEquals("/Images/13.png", efforts.get(7).getImgPath());
        assertEquals("∞", efforts.get(12).getEffortValue());
        assertEquals("/Images/infinity.png", efforts.get(12).getImgPath());
    }

    @Test // We are testing with the value 300, which we know are not in the table
    public void testReadByEffort_throwsSQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        effortDAO.readByEffort("300");
    }

    @Test
    public void testGetEffortList_Exception() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        effortDAO.getEffortList();
    }

}