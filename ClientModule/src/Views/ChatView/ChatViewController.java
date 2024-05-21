package Views.ChatView;

import Application.Session;
import Application.ViewModelFactory;
import DataTypes.Message;
import DataTypes.User;
import DataTypes.UserRoles.UserPermission;
import Views.PlanningPokerView.PlanningPokerViewController;
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
    @FXML TableColumn<User, String> roleColumn;
    @FXML Button setPOButton;
    @FXML Button setSMButton;
    @FXML private Button setAdminBtn;
    private PlanningPokerViewController parentController;

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
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleAsString"));

        viewModel.userProperty().addListener((obs, oldMessage, newMessage) -> {
            onUserReceived((ObservableList<User>) newMessage);
        });

        Platform.runLater(() -> {
        viewModel.loadUsers();
        });
        enableSpecificUIPermissionBasedElements();
    }

    public void setParentController(PlanningPokerViewController parentController) {
        this.parentController = parentController;
    }

    public void onMessageRecieved(String message)
    {
        //Handle the empty message, so empty messages doesnt get added to the chat, also fixes bug where the same message couldn't be sent twice in a row
        if (!message.equals("")) {
            Platform.runLater(() -> {
                chatTextArea.appendText(message + "\n");
            });
        }
    }

    public void onUserReceived(ObservableList<User> users)
    {
        if (!users.isEmpty()) {
            for (User user : users) {
                //System.out.println(user.getRoleAsString());
            }
            Platform.runLater(() -> {
                userTableView.getItems().clear();
                userTableView.getItems().addAll(users);
                enableSpecificUIPermissionBasedElements();
            });
        }
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

    public void onSetProductOwnerButtonPressed()
    {
        if (!userTableView.getSelectionModel().getSelectedItem().equals(Session.getCurrentUser()) || Session.getCurrentUser().getAdmin() != null) {
            viewModel.setProductOwner(userTableView.getSelectionModel().getSelectedItem());
        }
    }

    public void onSetScrumMasterButtonPressed()
    {
        viewModel.setScrumMaster(userTableView.getSelectionModel().getSelectedItem());
    }

    public void onSetAdminButtonPressed() {
        viewModel.setAdminOverride();
    }

    private void enableSpecificUIPermissionBasedElements()
    {
        setPOButton.setVisible(false);
        setSMButton.setVisible(false);
        // Enable permission to assign team roles, if proper user permission exists:
        if(Session.getCurrentUser().getRole().getPermissions().contains(UserPermission.ASSIGN_TEAM_ROLES)) {
            setPOButton.setVisible(true);
            setSMButton.setVisible(true);
        }
    }

    public void onStartCallButtonPressed()
    {
        viewModel.startVoiceCall();
    }

    public void onEndCallButtonPressed()
    {
        viewModel.endVoiceCall();
    }
}
