package Views.ChatView;

import Application.ViewModelFactory;
import DataTypes.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;

public class ChatViewController {
    private ChatViewModel viewModel;
    @FXML public TextField messageInputTextField;
    @FXML public TextArea chatTextArea;
    @FXML public Label userIdLabel;

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
    }

    public void onMessageRecieved(String message)
    {
        Platform.runLater(() -> {
            chatTextArea.appendText(message + "\n");
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
