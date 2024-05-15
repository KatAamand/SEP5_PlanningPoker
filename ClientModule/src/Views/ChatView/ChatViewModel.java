package Views.ChatView;

import Application.Session;
import DataTypes.Message;
import DataTypes.User;
import Model.Chat.ChatModel;
import Views.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ChatViewModel extends ViewModel {
    private final ChatModel chatModel;
    private StringProperty message = new SimpleStringProperty();
    private ListProperty<User> users = new SimpleListProperty<>();
    private final Session session;

    public ChatViewModel(ChatModel chatModel, Session session) {
        super();
        this.session = session;
        this.chatModel = chatModel;

        chatModel.addPropertyChangeListener("messageReceived", evt -> {
            Message message = (Message) evt.getNewValue();
            messageProperty().set(message.getMessage());
        });

        chatModel.addPropertyChangeListener("userReceived", evt -> {
            ArrayList<User> users = (ArrayList<User>) evt.getNewValue();
            System.out.println(users);
            ObservableList<User> observableList = FXCollections.observableArrayList(users);
            userProperty().set(observableList);
        });
    }

    public void setProductOwner(User user) {
        chatModel.setProductOwner(user);
    }

    public StringProperty messageProperty()
    {
        return message;
    }
    public ListProperty<User> userProperty()
    {
        return users;
    }
    public void loadUsers()
    {
        chatModel.loadUsers();
    }

    public void sendMessage(String message)
    {
        chatModel.sendMessage(new Message(Session.getCurrentUser().getUsername() + ": " + message), session.getCurrentUser());
    }


}
