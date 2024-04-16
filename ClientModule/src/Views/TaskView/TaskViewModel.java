package Views.TaskView;

import Model.Login.LoginModel;
import Model.Task.TaskModel;
import Views.ViewModel;

public class TaskViewModel extends ViewModel {
    private LoginModel clientModel;
    public TaskViewModel(TaskModel clientModel) {
        super();
        this.clientModel = clientModel;
    }
}
