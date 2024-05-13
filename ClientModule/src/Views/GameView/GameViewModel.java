package Views.GameView;

import Application.ModelFactory;
import Application.Session;
import DataTypes.Effort;
import DataTypes.Task;
import DataTypes.UserCardData;
import Model.Game.GameModel;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Objects;

public class GameViewModel {
    PropertyChangeSupport propertyChangeSupport;
    private final GameModel gameModel;
    private Property<String> taskHeaderProperty;
    private Property<String> taskDescProperty;
    private ArrayList<Effort> effortList;
    private Task displayedTask;
    @FXML
    public HBox placedCardsWrapper;
    public ArrayList<UserCardData> placedCards;

    public GameViewModel() throws RemoteException {
        this.gameModel = ModelFactory.getInstance().getGameModel();
        taskHeaderProperty = new SimpleStringProperty();
        taskDescProperty = new SimpleStringProperty();
        effortList = new ArrayList<>();
        getEffortList();

        placedCards = new ArrayList<>();

        //Assign listeners:
        propertyChangeSupport = new PropertyChangeSupport(this);
        gameModel.addPropertyChangeListener("placedCardReceived", evt -> updatePlacedCard((UserCardData) evt.getNewValue()));
        gameModel.addPropertyChangeListener("receivedListOfTasksToSkip", evt -> Platform.runLater(this::refresh));
        gameModel.addPropertyChangeListener("clearPlacedCards", evt -> Platform.runLater(this::clearPlacedCards));
    }

    private void clearPlacedCards() {
        placedCardsWrapper.getChildren().clear();
        placedCards.clear();
    }

    private void updatePlacedCard(UserCardData newValue) {
        System.out.println("ViewModel: Received placed card: " + newValue.getUsername() + " " + newValue.getPlacedCard());
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
        System.out.println("Placed cards list size: " + placedCards.size());
        updatePlacedCardsWrapper();
    }

    private void updatePlacedCardsWrapper() {
        Platform.runLater(() -> {
            placedCardsWrapper.getChildren().clear();  // Clear previous views to avoid stacking
            for (UserCardData card : placedCards) {
                System.out.println("Updating placed cards wrapper: " + card.getUsername() + " " + card.getPlacedCard());
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
        System.out.println("Creating card wrapper: imagePath: " + imagePath);
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        CustomImageView clone = new CustomImageView(image, card.getPlacedCard());
        clone.setFitHeight(140);
        clone.setFitWidth(105);

        cardWrapper.getChildren().addAll(clone, getBackImageView());
        newCardWrapper.getChildren().addAll(cardUsername, cardWrapper);
        return newCardWrapper;
    }

    private String getEffortImagePath(String value) {
        for (Effort effort : effortList) {
            if (effort.getEffortValue().equals(value)) {
                System.out.println("Found effort: " + effort.getEffortValue() + " " + effort.getImgPath());
                return effort.getImgPath();
            }
        }
        return null;
    }

    public void refresh() {
        Task nextTask = gameModel.nextTaskToEvaluate();

        displayedTask = gameModel.nextTaskToEvaluate();
        System.out.println("showing: " + displayedTask);

        if (nextTask != null) {
            taskHeaderPropertyProperty().setValue(nextTask.getTaskHeader());
            taskDescPropertyProperty().setValue(nextTask.getDescription());
        } else {
            taskHeaderPropertyProperty().setValue("No more tasks");
            taskDescPropertyProperty().setValue("No more tasks");
        }

        clearPlacedCards();
    }

    public void skipTask() {
        if (displayedTask != null) {
            gameModel.skipTask(displayedTask);
        }
        this.refresh();
    }

    public Property<String> taskHeaderPropertyProperty() {
        return taskHeaderProperty;
    }

    public Property<String> taskDescPropertyProperty() {
        return taskDescProperty;
    }

    public void getEffortList() {
        this.effortList = gameModel.getEffortList();
    }

    public void getPossiblePlayingCards(StackPane effortWrapper) {
        Platform.runLater(() -> {
            int counter = 0;
            for (Effort effort : effortList) {
                Image image = new Image(getClass().getResourceAsStream(effort.getImgPath()));
                CustomImageView imageView = new CustomImageView(image, effort.getEffortValue());
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
                effortWrapper.getChildren().add(imageView);
            }
        });
    }

    public void handleCardSelection(CustomImageView selectedCard, StackPane effortWrapper) {
        String currentUser = Session.getCurrentUser().getUsername();

        effortWrapper.getChildren().forEach(node -> node.getStyleClass().remove("card-selected"));
        selectedCard.getStyleClass().add("card-selected");

        handleCardPlacement(selectedCard, currentUser);
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

    public void setPlacedCardsWrapper(HBox placedCardsWrapper) {
        this.placedCardsWrapper = placedCardsWrapper;
    }

    public void requestPlacedCard(String username, String placedCard) {
        UserCardData placedCardData = new UserCardData(username, placedCard);
        gameModel.requestPlacedCard(placedCardData);
    }

    public void requestClearPlacedCards() {
        gameModel.requestClearPlacedCards();
    }
}
