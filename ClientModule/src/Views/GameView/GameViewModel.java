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
import java.util.HashMap;
import java.util.Map;

public class GameViewModel {
    PropertyChangeSupport propertyChangeSupport;
    private final GameModel gameModel;
    private Property<String> taskHeaderProperty;
    private Property<String> taskDescProperty;
    private ArrayList<Effort> effortList;
    private Task displayedTask;
    @FXML
    public HBox placedCardsWrapper;
    private ImageView selectedCard;
    VBox placedCardWrapper;
    private Map<String, VBox> clientCardMap;

    public GameViewModel() throws RemoteException {
        this.gameModel = ModelFactory.getInstance().getGameModel();
        taskHeaderProperty = new SimpleStringProperty();
        taskDescProperty = new SimpleStringProperty();
        effortList = new ArrayList<>();
        getEffortList();


        clientCardMap = new HashMap<>();
        placedCardWrapper = new VBox();

        //Assign listeners:
        propertyChangeSupport = new PropertyChangeSupport(this);
        propertyChangeSupport.addPropertyChangeListener("placedCardMap", evt -> updateClientCartMap((UserCardData) evt.getNewValue()));
        gameModel.addPropertyChangeListener("receivedListOfTasksToSkip", evt -> Platform.runLater(this::refresh));
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

    public void showPlayingCards(StackPane effortWrapper) {
        Platform.runLater(() -> {
            int counter = 0;
            for (Effort effort : effortList) {
                Image image = new Image(
                        getClass().getResourceAsStream(effort.getImgPath()));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(125);
                imageView.setFitWidth(90);

                imageView.setTranslateX(counter);
                counter += 60;

                // adding hover effects for cards
                imageView.setOnMouseEntered(
                        e -> imageView.getStyleClass().add("card-hover"));
                imageView.setOnMouseExited(
                        e -> imageView.getStyleClass().remove("card-hover"));

                imageView.setOnMouseClicked(
                        event -> handleCardSelection(imageView, effortWrapper));
                effortWrapper.getChildren().add(imageView);
            }
        });
    }

    public void handleCardSelection(ImageView selectedCard, StackPane effortWrapper) {
        placedCardWrapper = clientCardMap.get(Session.getCurrentUser().getUsername());

        // Removing styling from other cards
        for (Node node : effortWrapper.getChildren()) {
            node.getStyleClass().remove("card-selected");
        }

        // Adding shadow to selected card
        if (!selectedCard.getStyleClass().contains("card-selected")) {
            selectedCard.getStyleClass().add("card-selected");
        }

        if (placedCardWrapper == null) {
            placedCardWrapper = createPlacedCardWrapper(selectedCard, Session.getCurrentUser().getUsername());

            clientCardMap.put(Session.getCurrentUser().getUsername(), placedCardWrapper);
        } else {
            StackPane cardWrapper = (StackPane) placedCardWrapper.getChildren().get(1);
            ImageView clone = (ImageView) cardWrapper.getChildren().get(0);
            clone.setImage(selectedCard.getImage());
        }

        String placedCardValue = getCardValue(selectedCard.getImage().getUrl());

        requestPlacedCard(Session.getCurrentUser().getUsername(), placedCardValue);
    }

    private String getCardValue(String url) {
        String cardValue;
        for (Effort effort : effortList) {
            if (effort.getImgPath().equals(url)) {
                cardValue = effort.getEffortValue();
                return cardValue;
            }
        }
        return null;
    }

    private VBox createPlacedCardWrapper(ImageView selectedCard, String username) {
        VBox newCardWrapper = new VBox();
        newCardWrapper.alignmentProperty().setValue(javafx.geometry.Pos.CENTER);

        Label cardUsername = new Label(username);
        StackPane cardWrapper = new StackPane();

        ImageView clone = new ImageView(selectedCard.getImage());
        clone.setFitHeight(140);
        clone.setFitWidth(105);

        cardWrapper.getChildren().addAll(clone, getBackImageView());
        newCardWrapper.getChildren().addAll(cardUsername, cardWrapper);

        return newCardWrapper;
    }

    private VBox createPlacedCardWrapper(String selectedCardValue, String username) {
        VBox newCardWrapper = new VBox();
        newCardWrapper.alignmentProperty().setValue(javafx.geometry.Pos.CENTER);
        Label cardUsername = new Label(username);
        StackPane cardWrapper = new StackPane();

        Image image = new Image(getClass().getResourceAsStream(selectedCardValue));
        ImageView clone = new ImageView(image);
        clone.setFitHeight(140);
        clone.setFitWidth(105);

        newCardWrapper.getChildren().add(cardUsername);
        cardWrapper.getChildren().add(clone);
        cardWrapper.getChildren().add(getBackImageView());
        newCardWrapper.getChildren().add(cardWrapper);
        newCardWrapper.getChildren().add(newCardWrapper);

        return newCardWrapper;
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

    public void clearClientCartMap() {
        clientCardMap.clear();
    }

    public void updateClientCartMap(UserCardData placedCardData) {
        clientCardMap.get(placedCardData.getUsername()).getChildren().clear();
        clientCardMap.put(placedCardData.getUsername(), createPlacedCardWrapper(placedCardData.getPlacedCard(), placedCardData.getUsername()));
    }

    public void requestPlacedCard(String username, String placedCard) {
        UserCardData placedCardData = new UserCardData(username, placedCard);
        gameModel.requestPlacedCard(placedCardData);
    }
}
