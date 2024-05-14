package Database.User;

import DataTypes.User;

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

class UserDAOImplTest {

    private UserDAOImpl userDAO;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        userDAO = UserDAOImpl.getInstance();
        mockConnection = Mockito.mock(Connection.class);
        mockPreparedStatement = Mockito.mock(PreparedStatement.class);
        mockResultSet = Mockito.mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testCreateUser() throws SQLException {
        // arrange and act
        User user = userDAO.create("testuser", "testpass");

        // assert
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("testpass", user.getPassword());
    }

    @Test
    void testReadByLoginInfo_zero() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        User user = userDAO.readByLoginInfo("nonexistent", "wrongpassword");
        assertNull(user);
    }

    @Test
    void testReadByLoginInfo_one() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("username")).thenReturn("test");
        when(mockResultSet.getString("password")).thenReturn("1234");

        User user = userDAO.readByLoginInfo("test", "1234");
        assertNotNull(user);
        assertEquals("test", user.getUsername());
        assertEquals("1234", user.getPassword());
    }

    @Test // Testing for the 29 users currently created
    void testGetAllUsers_many() throws SQLException {
        when(mockResultSet.next()).thenReturn(true );

        ArrayList<User> users = userDAO.getAllUsers();
        assertEquals(29, users.size());
    }

    @Test // Testing if the User objects are created correctly
    void testGetAllUsers_objectsInTable() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);

        ArrayList<User> users = userDAO.getAllUsers();
        assertEquals("test", users.get(0).getUsername());
        assertEquals("1234", users.get(0).getPassword());
        assertEquals("test2", users.get(1).getUsername());
        assertEquals("1234", users.get(1).getPassword());
    }

    @Test
    void testCreateUser_zero_exception() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new IllegalArgumentException());

        userDAO.create("", "");
    }

    @Test
    void testGetAllUsers_exception() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        userDAO.getAllUsers();
    }
}