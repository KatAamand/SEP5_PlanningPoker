package Database.User;

import DataTypes.User;
import Database.Connection.DatabaseConnectionUser;


import java.sql.*;
import java.util.ArrayList;

public class UserDAOImpl extends DatabaseConnectionUser implements UserDAO {
    private static UserDAOImpl instance;

    private UserDAOImpl() throws SQLException {
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    public static synchronized UserDAOImpl getInstance() throws SQLException {
        if (instance == null) {
            instance = new UserDAOImpl();
        }
        return instance;
    }

    @Override
    public User create(String username, String password)
            throws SQLException {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "insert into \"user\"(username, password) VALUES (?,?);");
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
            return new User(username, password);
        }

    }

    @Override
    public User readByLoginInfo(String username, String password)
            throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM \"user\" WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String retrievedUsername = resultSet.getString("username");
                String retrievedPassword = resultSet.getString("password");
                return new User(retrievedUsername, retrievedPassword);
            } else {
                return null;
            }
        }
    }

    @Override
    public ArrayList<User> getAllUsers() throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"user\"");
            ResultSet resultSet = statement.executeQuery();
            ArrayList<User> users = new ArrayList<>();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                users.add(new User(username, password));
            }
            return users;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return super.getConnection();
    }
}
