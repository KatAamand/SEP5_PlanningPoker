package Database.PlanningPoker;

import DataTypes.PlanningPoker;
import DataTypes.Task;
import Database.Task.TaskDAO;
import Database.Task.TaskDAOImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) throws SQLException {
        TaskDAO taskDAO = TaskDAOImpl.getInstance();
        PlanningPokerDAO dao = PlanningPokerDAOImpl.getInstance();

        PlanningPoker newPlanningPoker = dao.create();
        Task newTask = taskDAO.create("test", "klfdsæfds", newPlanningPoker.getPlanningPokerID());
        Task newTask2 = taskDAO.create("test2", "klfdsæfds", newPlanningPoker.getPlanningPokerID());

        ArrayList<PlanningPoker> allPlanningPokers = dao.getAllPlanningPoker();

        for (PlanningPoker planningPoker : allPlanningPokers) {
            System.out.println(planningPoker.getPlanningPokerID());

            List<Task> tasklist = planningPoker.getTaskList();
            for (Task task : tasklist) {
                System.out.println(task.getTaskID());
            }
        }

    }
}
