package Database;

import Database.Task.TaskDAO;
import Database.Task.TaskDAOImpl;

import java.sql.SQLException;

public class Test {

    public static void main(String[] args) {

        try {
            TaskDAO taskDAO = TaskDAOImpl.getInstance();

            taskDAO.create("Task1", "Description1", 1);
            System.out.println(taskDAO.readByPlanningPokerId(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
