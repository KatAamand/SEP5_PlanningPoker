package Model.Game;

import DataTypes.Effort;
import DataTypes.Task;
import Util.PropertyChangeSubject;
import DataTypes.UserCardData;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * GameModel defines the interface to the model/class which is responsible for handling the 'estimation of effort' phase of the game, including assigning efforts to each task.
 * It extends the PropertyChangeSubject interface and is thus prepared for use with Observer Pattern principles.
 */
public interface GameModel extends PropertyChangeSubject {


    /** Primary initialization method of interfaced class. Intended for use in a JavaFx environment
     * where certain operations must be run on the javaFx thread in a Platform.runLater(() ->) method
     * in order to avoid errors and increase application stability.
     */
    void init();


    /** Returns the next task in the Task List, which has not already been assigned an effort - or which has not already been skipped.
     * If all tasks have been skipped then this method will reset the skipped list and return the first task which is not assigned an effort.
     * @return the next Task object from the current games Task List, which has not been assigned an effort - and has not been skipped.
     * Returns <code>NULL</code> if there are no more tasks in the list, which have not been assigned an effort.
     */
    Task nextTaskToEvaluate();


    // TODO: Source code comment/document
    ArrayList<Effort> getEffortList();


    /** Returns the list of tasks that have been skipped during the current Planning Poker games 'estimation of effort' phase.
     * @return an ArrayList<Task> containing all the tasks which have been skipped during the active game.
     * Returns an empty ArrayList if there are no tasks that have been skipped.
     */
    ArrayList<Task> getSkippedTaskList();


    /** Removes a task from the list of skipped tasks, if the task is found in the list. Does nothing, if the task does not exist in the list.
     * @param task The task object to remove from the list of skipped tasks.
     */
    void removeTaskFromSkippedList(Task task);


    /** Skipped the task which is currently being estimated on, during the 'estimate effort' phase of the current Planning Poker game.
     * This method only executes if the local user has the proper UserPermission, (UserPermission.SKIP_TASK).
     * Once skipped, this method ensures that the next applicable task is displayed for the connected users to then 'estimate an effort' on.
     * @param task The task object to skip.
     */
    void skipTask(Task task);


    // TODO: Source code comment/document
    void requestPlacedCard(UserCardData userCardData);


    // TODO: Source code comment/document
    void updatePlacedCardMap(PropertyChangeEvent propertyChangeEvent);


    /** This method queries the client/server connection for the latest, most up-to-date,
     * list of tasks assigned to the currently active Planning Poker game.
     */
    void refreshTaskList();


    // TODO: Source code comment/document
    void requestClearPlacedCards();


    // TODO: Source code comment/document
    void requestShowCards();


    // TODO: Source code comment/document
    void requestRecommendedEffort();
}
