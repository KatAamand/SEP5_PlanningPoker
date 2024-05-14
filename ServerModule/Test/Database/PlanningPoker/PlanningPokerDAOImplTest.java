package Database.PlanningPoker;

import DataTypes.PlanningPoker;
import DataTypes.User;
import Database.User.UserDAOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class PlanningPokerDAOImplTest {
    private PlanningPokerDAOImpl planningPokerDAO;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        planningPokerDAO = PlanningPokerDAOImpl.getInstance();
        mockConnection = Mockito.mock(Connection.class);
        mockPreparedStatement = Mockito.mock(PreparedStatement.class);
        mockResultSet = Mockito.mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void createPlanningPoker() throws SQLException {
        PlanningPoker planningpoker = planningPokerDAO.create();
        assertNotNull(planningpoker);
    }

    @Test
    void readByPlanningPoker() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("planning_pokerid")).thenReturn("101");

        PlanningPoker planningPoker = planningPokerDAO.readByPlanningPoker(101);
        assertNotNull(planningPoker);
        assertEquals("101", planningPoker.getPlanningPokerID());
    }

    @Test
    void readByPlanningPoker_zero() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        PlanningPoker planningPoker = planningPokerDAO.readByPlanningPoker(0);
        assertNull(planningPoker);
    }

    @Test // Testing for the currently 225 rows in the planning_poker table
    void getAllPlanningPoker_many() throws SQLException {
        ArrayList<PlanningPoker> planningPokers = planningPokerDAO.getAllPlanningPoker();
        assertEquals(225, planningPokers.size());
    }

    @Test // Something is not right with the PlanningPokerDAO class
    void getAllPlanningPoker_objectsInTable() throws SQLException {
        ArrayList<PlanningPoker> planningPokers = planningPokerDAO.getAllPlanningPoker();

        assertEquals(101, planningPokers.get(0).getPlanningPokerID());
        assertEquals(220, planningPokers.get(119).getPlanningPokerID());
        assertEquals(224, planningPokers.get(325).getPlanningPokerID());
    }

    @Test
    void readByPlanningPoker_IllegalArgument() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new IllegalArgumentException());
        PlanningPoker planningPoker = planningPokerDAO.readByPlanningPoker(0);
    }

    @Test
    void testGetAllPlanningPoker_exception() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException());
        planningPokerDAO.getAllPlanningPoker();
    }
}