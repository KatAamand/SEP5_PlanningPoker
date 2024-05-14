package Views.GameView;

import Application.ModelFactory;
import Application.Session;
import Application.ViewModelFactory;
import DataTypes.Effort;
import DataTypes.Task;
import DataTypes.UserCardData;
import Model.Game.GameModel;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
  private ArrayList<Effort> effortList;
  private Task displayedTask;
  @FXML public HBox placedCardsWrapper;
  public ArrayList<UserCardData> placedCards;
  private Button skipButtonRef;
  private boolean isGameStarted;

  public GameViewModel() throws RemoteException
  {
    this.gameModel = ModelFactory.getInstance().getGameModel();
    taskHeaderProperty = new SimpleStringProperty();
    taskDescProperty = new SimpleStringProperty();
    finalEffortLabelProperty = new SimpleStringProperty();
    effortList = new ArrayList<>();
    isGameStarted = false;
    getEffortList();

        placedCards = new ArrayList<>();

    //Assign listeners:
    propertyChangeSupport = new PropertyChangeSupport(this);
    gameModel.addPropertyChangeListener("placedCardReceived",     evt -> updatePlacedCard((UserCardData) evt.getNewValue()));
    gameModel.addPropertyChangeListener("receivedListOfTasksToSkip", evt -> Platform.runLater( () -> {
          this.refresh();
          try {
            ViewModelFactory.getInstance().getTaskViewModel().refresh();
          } catch (RemoteException e) {
            throw new RuntimeException();
          }}));
    gameModel.addPropertyChangeListener("clearPlacedCards",     evt -> Platform.runLater(this::clearPlacedCards));
    gameModel.addPropertyChangeListener("taskListUpdated", evt -> Platform.runLater(this::refresh));
  }


  public void refresh() {
    displayedTask = gameModel.nextTaskToEvaluate();
    if (displayedTask != null) {
        // Show the next non-skipped and non-estimated task in the game UI:
        taskHeaderPropertyProperty().setValue(displayedTask.getTaskHeader());
        taskDescPropertyProperty().setValue(displayedTask.getDescription());
        finalEffortLabelProperty().setValue(displayedTask.getFinalEffort());

        // Disable the skip button, if this is the last non-estimated task in the list:
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
            disableSkipButton();
        } else {
            // Enable the skip button, if this is not the last non-estimated task in the list:
            enableAndShowSkipButton();
        }
    } else {
        // Shows if there are no more valid tasks left for estimation:
        taskHeaderPropertyProperty().setValue("No more tasks");
        taskDescPropertyProperty().setValue("No more tasks");
        finalEffortLabelProperty().setValue("");

        // Disables the skip button when no further tasks are left for estimation:
        disableSkipButton();
    }
    clearPlacedCards();
  }

  public void skipTask() {
    if (displayedTask != null) {
        gameModel.skipTask(displayedTask);
    }
    this.refresh();
    gameModel.refreshTaskList();
  }

  public Task getDisplayedTask() {
      return this.displayedTask;
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
    Task nonEditedTask = displayedTask.copy();
    displayedTask.setFinalEffort(finalEffortvalue);
    try {
      ModelFactory.getInstance().getTaskModel().editTask(nonEditedTask, displayedTask);
      Platform.runLater(() -> gameModel.removeTaskFromSkippedList(displayedTask));
    } catch (RemoteException e) {
      throw new RuntimeException(e);
    }
  }

  public void getEffortList()
  {
    this.effortList = gameModel.getEffortList();
  }

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
        gameModel.requestClearPlacedCards();
    }

    public void showPlacedCards() {
        Platform.runLater(() -> {
            flipAllCards();
            ifAllPlacedCardsAreAlike();
        });
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
            setFinalEffort(firstCard);
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

    private void setFinalEffort(String effortValue) {
    displayedTask.setFinalEffort(effortValue);
  }

}