package Views.ChatView;

import Application.Session;
import DataTypes.Message;
import Model.Chat.ChatModel;
import Views.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChatViewModel extends ViewModel {
    private final ChatModel chatModel;
    private StringProperty message = new SimpleStringProperty();
    private final Session session;

    public ChatViewModel(ChatModel chatModel, Session session) {
        super();
        this.session = session;
        this.chatModel = chatModel;

        chatModel.addPropertyChangeListener("messageReceived", evt -> {
            Message message = (Message) evt.getNewValue();
            messageProperty().set(message.getMessage());
        });
    }

    public StringProperty messageProperty()
    {
        return message;
    }

    public void sendMessage(String message)
    {
        chatModel.sendMessage(new Message(Session.getCurrentUser().getUsername() + ": " + message), session.getCurrentUser());
    }


}
