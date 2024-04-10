package Views.ChatView;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatViewController {
    private ChatViewModel viewModel;

    @FXML public TextField messageInputTextField;
    @FXML public TextArea chatTextArea;
    @FXML public Label userIdLabel;

    public ChatViewController(ChatViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void initialize() {
        chatViewModel.messageProperty().addListener((obs, oldMessage, newMessage) -> {
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
        String message = messageInputTextField.getText();
        chatViewModel.sendMessage(chatViewModel.getCurrentClient(), message);
        messageInputTextField.clear();
    }
}
