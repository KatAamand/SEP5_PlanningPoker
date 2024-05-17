package Model.Game;

import DataTypes.Effort;
import DataTypes.Task;
import Util.PropertyChangeSubject;
import DataTypes.UserCardData;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

public interface GameModel extends PropertyChangeSubject {
    /**
     * Primary initialization method. Should be initialized shortly after any constructor, or run inside a
     * Platform.runLater() method to ensure increase thread safety with javaFx.
     */
    void init();

    Task nextTaskToEvaluate();

    ArrayList<Effort> getEffortList();
    ArrayList<Task> getSkippedTaskList();
    void removeTaskFromSkippedList(Task task);

    void skipTask(Task task);

    void requestPlacedCard(UserCardData userCardData);

    void updatePlacedCardMap(PropertyChangeEvent propertyChangeEvent);

    void refreshTaskList();
    void requestClearPlacedCards();

    void requestShowCards();

    void requestRecommendedEffort();
}
