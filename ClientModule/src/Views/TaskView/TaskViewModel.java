package Views.TaskView;

import Model.Task.TaskModel;
import Views.ViewModel;

public class TaskViewModel extends ViewModel {
    private final TaskModel taskModel;
    public TaskViewModel(TaskModel taskModel) {
        super();
        this.taskModel = taskModel;
    }
}
