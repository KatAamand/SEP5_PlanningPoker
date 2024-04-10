package Views.ChatView;

import Model.ClientModel;
import Views.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChatViewModel extends ViewModel {
    private StringProperty message = new SimpleStringProperty();

    public ChatViewModel(ClientModel model) {
        super(model);

        super.getModel().addPropertyChangeListener("message", event -> {
            String message = (String) event.getNewValue();
            messageProperty().set(message);
        });
    }

    public StringProperty messageProperty()
    {
        return message;
    }

    public void sendMessage(String message)
    {
        super.getModel().sendMessage(message);
    }


}
