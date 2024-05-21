package Views.ChatView;

import Application.Session;
import DataTypes.Message;
import DataTypes.User;
import Model.Chat.ChatModel;
import Views.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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


    public void setScrumMaster(User user) {
        chatModel.setScrumMaster(user);
    }

    public void setAdminOverride() {
        // Check if the user is already an admin and still has all the proper admin privileges:
        if(Session.getCurrentUser().getAdmin() != null && Session.getCurrentUser().getRole().getPermissions().containsAll(Session.getCurrentUser().getAdmin().getPermissions())) {
            // Local user is already an admin, and has the proper permissions. Show an error.
            // User has all the proper permissions. Show an error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("You are already an ADMIN");
            alert.showAndWait();
        } else {
            // Local user is not an admin
            // Create a popup window for the user to enter the admin override password
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Admin Override");

            Label passwordLabel = new Label("Enter Override Password:");
            Label errorLabel = new Label("");
            PasswordField passwordField = new PasswordField();
            Button enterButton = new Button("Enter");
            Button cancelButton = new Button("Cancel");
            passwordField.setText("admin");

            enterButton.setOnAction(e -> {
                String password = passwordField.getText();
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", ChatViewModel: Admin Override password entered: " + password);
                // Attempt to set the local user as admin:
                if(chatModel.setAdmin(Session.getCurrentUser(), password)) {
                    // Close the window, the action was successful.
                    errorLabel.textProperty().setValue("");
                    popupStage.close();
                } else {
                    // Show an error.
                    errorLabel.textProperty().setValue("Incorrect override password");
                }
            });

            cancelButton.setOnAction(e -> popupStage.close());

            VBox mainVBox = new VBox();
            HBox passwordHBox = new HBox();
            passwordHBox.setAlignment(Pos.CENTER);
            passwordHBox.getChildren().add(passwordLabel);
            passwordHBox.getChildren().add(passwordField);
            HBox.setMargin(passwordLabel, new Insets(2.5, 2.5, 5, 5));
            HBox.setMargin(passwordField, new Insets(2.5, 5, 5, 2.5));
            mainVBox.getChildren().add(passwordHBox);

            mainVBox.getChildren().add(errorLabel);

            HBox buttonHBox = new HBox();
            buttonHBox.setAlignment(Pos.CENTER);
            buttonHBox.getChildren().add(enterButton);
            buttonHBox.getChildren().add(cancelButton);
            HBox.setMargin(enterButton, new Insets(2.5, 5, 5, 10));
            HBox.setMargin(cancelButton, new Insets(2.5, 10, 5, 5));
            mainVBox.getChildren().add(buttonHBox);

            Scene popupScene = new Scene(mainVBox, 300, 100);
            popupStage.setScene(popupScene);
            popupStage.showAndWait();
        }
    }

    public void startVoiceCall() {
        chatModel.startVoiceCall();
    }

    public void endVoiceCall() {
        chatModel.endVoiceCall();
    }
}
