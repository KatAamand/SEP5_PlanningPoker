package Views.ChatView;

import Application.ViewModelFactory;
import DataTypes.Message;
import DataTypes.User;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ChatViewController {
    private ChatViewModel viewModel;
    @FXML public TextField messageInputTextField;
    @FXML public TextArea chatTextArea;
    @FXML public Label userIdLabel;
    @FXML TableView<User> userTableView;
    @FXML TableColumn<User, String> userColumn;
    @FXML TableColumn<User, String> connectionColumn;

    public ChatViewController() {
        try {
            this.viewModel = ViewModelFactory.getInstance().getChatViewModel();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        viewModel.messageProperty().addListener((obs, oldMessage, newMessage) -> {
            onMessageRecieved(newMessage);
        });

        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        viewModel.userProperty().addListener((obs, oldMessage, newMessage) -> {
            onUserReceived((ObservableList<User>) newMessage);
        });
    }

    public void onMessageRecieved(String message)
    {
        Platform.runLater(() -> {
            chatTextArea.appendText(message + "\n");
        });
    }

    public void onUserReceived(ObservableList<User> users)
    {
        Platform.runLater(() -> {
            userTableView.getItems().clear();
            userTableView.getItems().addAll(users);
        });
    }

    public void onMessageSendButtonPressed()
    {
        if (!messageInputTextField.getText().isEmpty())
        {
            String message = messageInputTextField.getText();
            viewModel.sendMessage(message);
            messageInputTextField.clear();
        }

    }
}
