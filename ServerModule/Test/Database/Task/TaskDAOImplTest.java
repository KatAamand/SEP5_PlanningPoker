package Database.Task;

import DataTypes.Task;
import Database.PlanningPoker.PlanningPokerDAOImpl;
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
import static org.mockito.Mockito.*;

class TaskDAOImplTest {

    private TaskDAO taskDAO;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private final String testHeader = "TestHeader";
    private final String testdesc = "TestDescription";
    private final int testPlanningPokerId = 494;

    @BeforeEach
    void setUp() throws SQLException {
        taskDAO = TaskDAOImpl.getInstance();
        mockConnection = Mockito.mock(Connection.class);
        mockPreparedStatement = Mockito.mock(PreparedStatement.class);
        mockResultSet = Mockito.mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void createTask() throws SQLException {
        Task task = taskDAO.create(testHeader, testdesc, testPlanningPokerId);
        assertNotNull(task);
    }

    @Test
    void updateTask() throws SQLException {
        Task task = taskDAO.create(testHeader, testdesc, testPlanningPokerId);
        int taskId = task.getTaskID();

        task.setTaskHeader("Updated Header");
        taskDAO.update(task);

        Task updatedTask = taskDAO.readByTaskId(taskId);
        assertEquals("Updated Header", updatedTask.getTaskHeader());
    }

    @Test
    void readByPlanningPokerId() throws SQLException {
        ArrayList<Task> tasks = taskDAO.readByPlanningPokerId(testPlanningPokerId);
        assertNotNull(tasks);
    }

    @Test
    void delete() throws SQLException {
        Task task = taskDAO.create(testHeader, testdesc, testPlanningPokerId);
        int taskID = task.getTaskID();

        taskDAO.delete(task);
        assertThrows(SQLException.class, () -> taskDAO.readByTaskId(taskID));
    }
}