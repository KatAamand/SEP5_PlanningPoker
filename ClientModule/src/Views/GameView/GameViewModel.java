package Views.GameView;

import Application.ModelFactory;
import Application.Session;
import Application.ViewModelFactory;
import DataTypes.Effort;
import DataTypes.Task;
import DataTypes.User;
import DataTypes.UserCardData;
import DataTypes.UserRoles.UserPermission;
import DataTypes.UserRoles.UserRole;
import Model.Game.GameModel;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class GameViewModel
{
  PropertyChangeSupport propertyChangeSupport;
  private final GameModel gameModel;
  private Property<String> taskHeaderProperty;
  private Property<String> taskDescProperty;
  private Property<String> finalEffortLabelProperty;
  private Property<String> recommendedFinalEffortProperty;
  private ArrayList<Effort> effortList;
  private Task taskBeingEstimated;
  @FXML public HBox placedCardsWrapper;
  public ArrayList<UserCardData> placedCards;
  private Button skipButtonRef;
  private ChoiceBox finalEffortDropdownRef;
  private Button setEffortButtonRef;
  private Button clearCardsButtonRef;
  private Button showCardsButtonRef;
  private boolean isGameStarted;

  public GameViewModel() throws RemoteException
  {
    this.gameModel = ModelFactory.getInstance().getGameModel();
    taskHeaderProperty = new SimpleStringProperty();
    taskDescProperty = new SimpleStringProperty();
    finalEffortLabelProperty = new SimpleStringProperty();
    recommendedFinalEffortProperty = new SimpleStringProperty();

    effortList = new ArrayList<>();
    isGameStarted = false;
    getEffortList();

        placedCards = new ArrayList<>();

    //Assign listeners:
    propertyChangeSupport = new PropertyChangeSupport(this);
    gameModel.addPropertyChangeListener("placedCardReceived",     evt -> updatePlacedCard((UserCardData) evt.getNewValue()));
    gameModel.addPropertyChangeListener("recommendedEffortReceived", evt -> Platform.runLater(() -> showRecommendedEffort((String) evt.getNewValue())));
    gameModel.addPropertyChangeListener("receivedListOfTasksToSkip", evt -> Platform.runLater( () -> {
          this.refresh();
          try {
            ViewModelFactory.getInstance().getTaskViewModel().refresh();
          } catch (RemoteException e) {
            throw new RuntimeException();
          }
    }));
    gameModel.addPropertyChangeListener("clearPlacedCards",     evt -> Platform.runLater(this::clearPlacedCards));
    gameModel.addPropertyChangeListener("taskListUpdated", evt -> Platform.runLater(this::refresh));
    gameModel.addPropertyChangeListener("showCards", evt -> Platform.runLater(this::showPlacedCards));
    gameModel.addPropertyChangeListener("PlanningPokerObjUpdated", evt -> Platform.runLater(this::refresh));
  }


  public void refresh() {
    // Disable UI elements that are not available to the user, based on the users role / permissions:
    this.disableAllPermissionBasedUIInteractionElements();

    // Enable specific UI elements based on user role:
    this.enableSpecificUIPermissionBasedElements(Session.getCurrentUser());

    // Determine which task to show in the Game UI for estimation:
    Task selectedTaskInTaskList = null;
    taskBeingEstimated = gameModel.nextTaskToEvaluate();
    try {
      selectedTaskInTaskList = ViewModelFactory.getInstance().getTaskViewModel().getSelectedTask();
    } catch (RemoteException e) {
      throw new RuntimeException(e);
    }
    if(selectedTaskInTaskList != null) {
      // Local user has manually selected another task. Continue to display this task.
      taskHeaderPropertyProperty().setValue(selectedTaskInTaskList.getTaskHeader());
      taskDescPropertyProperty().setValue(selectedTaskInTaskList.getDescription());
      finalEffortLabelProperty().setValue(selectedTaskInTaskList.getFinalEffort());

    } else {
      // Local user has NOT manually selected another task. Display the task that the team is currently estimating.
      if (taskBeingEstimated != null) {
        // Show the next non-skipped and non-estimated task in the game UI:
        taskHeaderPropertyProperty().setValue(taskBeingEstimated.getTaskHeader());
        taskDescPropertyProperty().setValue(taskBeingEstimated.getDescription());
        finalEffortLabelProperty().setValue(taskBeingEstimated.getFinalEffort());
      } else {
        // Shows if there are no more valid tasks left for estimation:
        taskHeaderPropertyProperty().setValue("No more tasks");
        taskDescPropertyProperty().setValue("No more tasks");
        finalEffortLabelProperty().setValue("");
      }
    }
    clearPlacedCards();
  }

  public void disableAllPermissionBasedUIInteractionElements() {
    // Disable all Interaction based UI elements, in order to later re-enable them for each specific role:
    this.disableAndHideSkipButton();
    this.disableAndHidefinalEffortDropdown();
    this.disableAndHideSetEffortButton();
    this.disableAndHideClearCardsButton();
    this.disableAndHideShowCardsButton();
  }

  /** Used for enabling specific UI elements and interactions based on the users permissions */
  private void enableSpecificUIPermissionBasedElements(User user) {
    // Enable Final Effort interactions, if proper user permissions exist:
    if(user.getRole().getPermissions().contains(UserPermission.ASSIGN_FINAL_EFFORT)) {
      this.enableAndShowfinalEffortDropdownRef();
      this.enableAndShowSetEffortButton();
    }

    // Enable Skip Button interactions, if proper user permissions exist:
    if(user.getRole().getPermissions().contains(UserPermission.SKIP_TASK)) {
      if(checkIfSkipTaskBTNShouldBeEnabled()) {
        this.enableAndShowSkipButton();
      }
    }

    // Enable Reveal Efforts Button interactions, if proper user permissions exist:
    if(user.getRole().getPermissions().contains(UserPermission.REVEAL_USER_EFFORTS)) {
      this.enableAndShow_ShowCardsButton();
    }

    // Enable Clear Efforts Button interactions, if proper user permissions exist:
    if(user.getRole().getPermissions().contains(UserPermission.CLEAR_USER_EFFORTS)) {
      this.enableAndShowClearCardsButton();
    }

    // Enable permission to set a game password, if proper user permissions exists:
    if(user.getRole().getPermissions().contains(UserPermission.SET_GAME_PASSWORD)) {
      // TODO: Not implemented yet. This might not be the right class for this check. Maybe PlanningPokerViewModel is better?
      //  Depends on where the UI button/dropdown/etc to set the game password will be located.
    }

    // Enable permission to EXPORT a list of tasks, if proper user permission exists:
    if(user.getRole().getPermissions().contains(UserPermission.EXPORT_TASKLIST)) {
      // TODO: Not implemented yet. This might not be the right class for this check. Maybe PlanningPokerViewModel is better?
      //  Depends on where the UI button/dropdown/etc to export will be located.
    }

    // Enable permission to IMPORT a list of tasks, if proper user permission exists:
    if(user.getRole().getPermissions().contains(UserPermission.IMPORT_TASKLIST)) {
      // TODO: Not implemented yet. This might not be the right class for this check. Maybe PlanningPokerViewModel is better?
      //  Depends on where the UI button/dropdown/etc to import will be located.
    }
  }

  /** Returns false if the skip button should be deactivated and true if it should be activated - based on the number of tasks remaining to be estimated on. */
  private boolean checkIfSkipTaskBTNShouldBeEnabled() {
    if (taskBeingEstimated != null) {
      List<Task> taskList;
      try {
        taskList = ModelFactory.getInstance().getTaskModel().getTaskList();
      } catch (RemoteException e) {
        throw new RuntimeException();
      }
      // Count how many non-estimated tasks are still in the list:
      int numberOfNonEstimatedTasks = 0;
      if (taskList != null) {
        for (Task task : taskList) {
          if(task.getFinalEffort() != null && task.getFinalEffort().isEmpty()) {
            numberOfNonEstimatedTasks++;
          }
        }
      }
      // Disable the skip button if there is only 1 non-estimated task left:
      if(numberOfNonEstimatedTasks == 1) {
        return false;
      } else {
        // Enable the skip button, if this is not the last non-estimated task in the list:
        return true;
      }
    } else {
      // Disables the skip button when no further tasks are left for estimation:
      return false;
    }
  }

  public void skipTask() {
    if (taskBeingEstimated != null && Session.getCurrentUser().getRole().getPermissions().contains(UserPermission.SKIP_TASK)) {
        gameModel.skipTask(taskBeingEstimated);
    }
    this.refresh();
    gameModel.refreshTaskList();
  }

  public Task getTaskBeingEstimated() {
      return this.taskBeingEstimated;
  }

  public Property<String> taskHeaderPropertyProperty()
  {
    return taskHeaderProperty;
  }

  public Property<String> taskDescPropertyProperty()
  {
    return taskDescProperty;
  }

  public Property<String> finalEffortLabelProperty()
  {
    return finalEffortLabelProperty;
  }

  public void setFinalEffortLabel(String finalEffortvalue) {
    if(Session.getCurrentUser().getRole().getUserRole() == UserRole.SCRUM_MASTER) {
      Task nonEditedTask = taskBeingEstimated.copy();
      taskBeingEstimated.setFinalEffort(finalEffortvalue);
      try {
        ModelFactory.getInstance().getTaskModel().editTask(nonEditedTask, taskBeingEstimated);
        Platform.runLater(() -> gameModel.removeTaskFromSkippedList(taskBeingEstimated));
        finalEffortDropdownRef.setValue(null);
      } catch (RemoteException e) {
        throw new RuntimeException(e);
      }
    } else {
      System.out.println("GameViewModel: Local user attempted to set final effort. This action is reserved for the Scrum Master.");
    }
  }

  public void getEffortList()
  {
    this.effortList = gameModel.getEffortList();
  }

  // Button related methods
  public void setSkipButtonRef(Button skipButtonRef) {
    this.skipButtonRef = skipButtonRef;
  }

  public void disableAndHideSkipButton() {
    this.disableSkipButton();
    this.skipButtonRef.setVisible(false);
  }

  public void enableAndShowSkipButton() {
    this.enableSkipButton();
    this.skipButtonRef.setVisible(true);
  }

  public void enableSkipButton() {
    this.skipButtonRef.setDisable(false);
  }

  public void disableSkipButton() {
    this.skipButtonRef.setDisable(true);
  }

  public void setGameStarted(boolean bool) {
    this.isGameStarted = bool;
  }

  public boolean getGameStartStatus() {
    return this.isGameStarted;
  }

  public void setClearCardsButtonRef(Button clearCardsButtonRef) {
    this.clearCardsButtonRef = clearCardsButtonRef;
  }

  public void disableAndHideClearCardsButton() {
    this.clearCardsButtonRef.setDisable(true);
    this.clearCardsButtonRef.setVisible(false);
  }

  public void enableAndShowClearCardsButton() {
    this.clearCardsButtonRef.setDisable(false);
    this.clearCardsButtonRef.setVisible(true);
  }

  public void setShowCardsButtonRef(Button showCardsButtonRef) {
    this.showCardsButtonRef = showCardsButtonRef;
  }

  public void disableAndHideShowCardsButton() {
    this.showCardsButtonRef.setDisable(true);
    this.showCardsButtonRef.setVisible(false);
  }

  public void enableAndShow_ShowCardsButton() {
    this.showCardsButtonRef.setDisable(false);
    this.showCardsButtonRef.setVisible(true);
  }

  public void setFinalEffortDropdownRef(ChoiceBox finalEffortDropdownRef) {
    this.finalEffortDropdownRef = finalEffortDropdownRef;
  }

  public void disableAndHidefinalEffortDropdown() {
    this.finalEffortDropdownRef.setDisable(true);
    this.finalEffortDropdownRef.setVisible(false);
  }

  public void enableAndShowfinalEffortDropdownRef() {
    this.finalEffortDropdownRef.setDisable(false);
    this.finalEffortDropdownRef.setVisible(true);
  }

  public void setEffortButtonRef(Button setEffortButtonRef) {
    this.setEffortButtonRef = setEffortButtonRef;
  }

  public void disableAndHideSetEffortButton() {
    this.setEffortButtonRef.setDisable(true);
    this.setEffortButtonRef.setVisible(false);
  }

  public void enableAndShowSetEffortButton() {
    this.setEffortButtonRef.setDisable(false);
    this.setEffortButtonRef.setVisible(true);
  }

  // Effort and playing card methods:

  public ObservableList<String> getEffortObserverList()
  {
   ObservableList<String> efforts = javafx.collections.FXCollections.observableArrayList();
   for (Effort effort : effortList)
   {
     efforts.add(effort.getEffortValue());
   }
   return efforts;
  }

  public void getPossiblePlayingCards(StackPane effortWrapper) {
      Platform.runLater(() -> {
          int counter = 0;
          for (Effort effort : effortList) {
              Image image = new Image(getClass().getResourceAsStream(effort.getImgPath()));
              CustomImageView imageView = new CustomImageViewAdapter(image, effort.getEffortValue());
              imageView.setFitHeight(125);
              imageView.setFitWidth(90);

            imageView.setTranslateX(counter);
            counter += 60;

            // adding hover effects for cards
            imageView.setOnMouseEntered(
                    e -> imageView.getStyleClass().add("card-hover"));
            imageView.setOnMouseExited(
                    e -> imageView.getStyleClass().remove("card-hover"));

            imageView.setOnMouseClicked(event -> handleCardSelection(imageView, effortWrapper));
            effortWrapper.getChildren().add((Node) imageView);
        }
    });
  }

  public void handleCardSelection(CustomImageView selectedCard, StackPane effortWrapper) {
    String currentUser = Session.getCurrentUser().getUsername();

    effortWrapper.getChildren().forEach(node -> node.getStyleClass().remove("card-selected"));
    selectedCard.getStyleClass().add("card-selected");

    handleCardPlacement(selectedCard, currentUser);
  }

    public void setPlacedCardsWrapper(HBox placedCardsWrapper) {
        this.placedCardsWrapper = placedCardsWrapper;
    }

    // Request methods
    public void requestPlacedCard(String username, String placedCard) {
        UserCardData placedCardData = new UserCardData(username, placedCard);
        gameModel.requestPlacedCard(placedCardData);
    }

  public void requestClearPlacedCards() {
    if(Session.getCurrentUser().getRole().getPermissions().contains(UserPermission.CLEAR_USER_EFFORTS)) {
      gameModel.requestClearPlacedCards();
    }
  }

    public void showPlacedCards() {
        flipAllCards();
        ifAllPlacedCardsAreAlike();
        requestRecommendedEffort();
    }

    private void requestRecommendedEffort() {
        gameModel.requestRecommendedEffort();
    }

    private void showRecommendedEffort(String value) {
      recommendedFinalEffortProperty.setValue(value);
      finalEffortDropdownRef.setValue(value);
    }


    // helper functions
    private void ifAllPlacedCardsAreAlike() {
        if (placedCards.size() > 1) {
            String firstCard = placedCards.get(0).getPlacedCard();
            for (UserCardData card : placedCards) {
                System.out.println("card: " + card.getPlacedCard() + " firstCard: " + firstCard);
                if (!firstCard.equals(card.getPlacedCard())) {
                    System.out.println("cards are not the same");
                    return;
                }
            }
            finalEffortDropdownRef.setValue(firstCard);
            activateHulaDancers();
        }
    }

    private void handleCardPlacement(CustomImageView selectedCard, String currentUser) {
        String cardValue = selectedCard.getEffortValue();
        requestPlacedCard(currentUser, cardValue);
    }

    private Node getBackImageView() {
        Image back = new Image(getClass().getResourceAsStream("/Images/back.jpg"));
        ImageView backImageView = new ImageView(back);
        backImageView.setFitHeight(140);
        backImageView.setFitWidth(105);
        return backImageView;
    }

    private void updatePlacedCardsWrapper() {
        Platform.runLater(() -> {
            placedCardsWrapper.getChildren().clear();  // Clear previous views to avoid stacking
            placedCardsWrapper.setSpacing(5);
            for (UserCardData card : placedCards) {
                VBox cardWrapper = createCardWrapper(card);
                placedCardsWrapper.getChildren().add(cardWrapper);
            }
        });
    }

    private VBox createCardWrapper(UserCardData card) {
        VBox newCardWrapper = new VBox();
        newCardWrapper.alignmentProperty().setValue(javafx.geometry.Pos.CENTER);
        Label cardUsername = new Label(card.getUsername());
        StackPane cardWrapper = new StackPane();

        String imagePath = getEffortImagePath(card.getPlacedCard());
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        CustomImageViewAdapter frontImageView = new CustomImageViewAdapter(image, card.getPlacedCard());
        frontImageView.setFitHeight(140);
        frontImageView.setFitWidth(105);
        frontImageView.setVisible(false);

        ImageView backImageView = (ImageView) getBackImageView();
        backImageView.setVisible(true);

        cardWrapper.getChildren().addAll(frontImageView, backImageView);
        newCardWrapper.getChildren().addAll(cardUsername, cardWrapper);

        cardWrapper.getStyleClass().add("card-flip");
        frontImageView.getStyleClass().add("card-front");
        backImageView.getStyleClass().add("card-back");

        return newCardWrapper;
    }

    private void flipAllCards() {
        for (Node node : placedCardsWrapper.getChildren()) {
            if (node instanceof VBox) {
                VBox vbox = (VBox) node;
                if (!vbox.getChildren().isEmpty() && vbox.getChildren().get(1) instanceof StackPane) {
                    StackPane stackPane = (StackPane) vbox.getChildren().get(1);
                    if (stackPane.getChildren().size() == 2) {
                        ImageView front = (ImageView) stackPane.getChildren().get(1);
                        ImageView back = (ImageView) stackPane.getChildren().get(0);

                        flipCard(front, back);
                    }
                }
            }
        }
    }

    private void flipCard(ImageView front, ImageView back) {
        if (front.isVisible()) {
            // If the front is currently visible, hide it and show the back
            ScaleTransition hideFront = new ScaleTransition(Duration.seconds(0.3), front);
            hideFront.setFromX(1);
            hideFront.setToX(0);
            hideFront.setOnFinished(e -> {
                front.setVisible(false);
                back.setVisible(true);
                ScaleTransition showBack = new ScaleTransition(Duration.seconds(0.3), back);
                showBack.setFromX(0);
                showBack.setToX(1);
                showBack.play();
            });
            hideFront.play();
        } else {
            // If the back is currently visible, hide it and show the front
            ScaleTransition hideBack = new ScaleTransition(Duration.seconds(0.3), back);
            hideBack.setFromX(1);
            hideBack.setToX(0);
            hideBack.setOnFinished(e -> {
                back.setVisible(false);
                front.setVisible(true);
                ScaleTransition showFront = new ScaleTransition(Duration.seconds(0.3), front);
                showFront.setFromX(0);
                showFront.setToX(1);
                showFront.play();
            });
            hideBack.play();
        }


    }

    private String getEffortImagePath(String value) {
        for (Effort effort : effortList) {
            if (effort.getEffortValue().equals(value)) {
                return effort.getImgPath();
            }
        }
        return null;
    }

    private void clearPlacedCards() {
        placedCardsWrapper.getChildren().clear();
        placedCards.clear();
    }

    private void updatePlacedCard(UserCardData newValue) {
        boolean found = false;
        for (UserCardData card : placedCards) {
            if (card.getUsername().equals(newValue.getUsername())) {
                int i = placedCards.indexOf(card);
                placedCards.set(i, newValue);
                found = true;
                break;
            }
        }
        if (!found) {
            placedCards.add(newValue);
        }
        updatePlacedCardsWrapper();
    }

    private void activateHulaDancers() {
        System.out.println("Hula dancers activated");
        HBox hulaContainer = new HBox();

        Image hulaGIF = new Image(getClass().getResourceAsStream("/Images/hula_girls.gif"));
        ImageView hulaView = new ImageView(hulaGIF);
        hulaView.setFitHeight(300);
        hulaView.setFitWidth(300);

        hulaContainer.getChildren().add(hulaView);
        placedCardsWrapper.getChildren().add(hulaContainer);
    }

    public void requestShowCards() {
      gameModel.requestShowCards();
    }

    public Property<String> recommendedEffortProperty() {
        return recommendedFinalEffortProperty;
    }
}