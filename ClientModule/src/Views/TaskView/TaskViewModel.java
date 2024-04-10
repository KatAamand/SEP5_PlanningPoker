package Views.TaskView;

import Model.ClientModel;
import Views.ViewModel;

public class TaskViewModel extends ViewModel {
    private ClientModel clientModel;
    public TaskViewModel(ClientModel clientModel) {
        super();
        this.clientModel = clientModel;
    }
}
