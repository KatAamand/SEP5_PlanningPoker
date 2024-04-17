package Views.ChatView;

import Model.Chat.ChatModel;
import Views.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChatViewModel extends ViewModel {
    private final ChatModel chatModel;
    private StringProperty message = new SimpleStringProperty();

    public ChatViewModel(ChatModel chatModel) {
        super();
        this.chatModel = chatModel;

        chatModel.addPropertyChangeListener("messageReceived", evt -> {
            String message = (String) evt.getNewValue();
            messageProperty().set(message);
        });
    }

    public StringProperty messageProperty()
    {
        return message;
    }

    public void sendMessage(String message)
    {
        chatModel.sendMessage(Session.getCurrentUser.getUserName + ": " + message, Session.getCurrentUser);
    }


}
