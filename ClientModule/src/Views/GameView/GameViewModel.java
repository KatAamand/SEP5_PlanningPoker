package Views.GameView;

import Application.ModelFactory;
import Application.Session;
import DataTypes.Effort;
import DataTypes.Task;
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

import java.rmi.RemoteException;
import java.util.ArrayList;

public class GameViewModel
{
  private final GameModel gameModel;
  private Property<String> taskHeaderProperty;
  private Property<String> taskDescProperty;
  private ArrayList<Effort> effortList;
  @FXML public HBox placedCardsWrapper;
  private ImageView selectedCard;
  private ImageView backImageView;

  public GameViewModel() throws RemoteException
  {
    this.gameModel = ModelFactory.getInstance().getGameModel();
    taskHeaderProperty = new SimpleStringProperty();
    taskDescProperty = new SimpleStringProperty();
    effortList = new ArrayList<>();
    getEffortList();
  }

  public void refresh()
  {
    Task nextTask = gameModel.nextTaskToEvaluate();

    if (nextTask != null)
    {
      taskHeaderPropertyProperty().setValue(nextTask.getTaskHeader());
      taskDescPropertyProperty().setValue(nextTask.getDescription());
    }
    else
    {
      taskHeaderPropertyProperty().setValue("No more tasks");
      taskDescPropertyProperty().setValue("No more tasks");
    }
  }

  public Property<String> taskHeaderPropertyProperty()
  {
    return taskHeaderProperty;
  }

  public Property<String> taskDescPropertyProperty()
  {
    return taskDescProperty;
  }

  public void getEffortList()
  {
    this.effortList = gameModel.getEffortList();
  }

  public void showPlayingCards(StackPane effortWrapper)
  {
    Platform.runLater(() -> {
      int counter = 0;
      for (Effort effort : effortList)
      {
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

  public void handleCardSelection(ImageView selectedCard,
      StackPane effortWrapper)
  {
    if (this.selectedCard != selectedCard)
    {
      this.selectedCard = selectedCard;

      // Removing styling from other cards
      for (Node node : effortWrapper.getChildren())
      {
        node.getStyleClass().remove("card-selected");
      }

      // Adding shadow to selected card
      if (!selectedCard.getStyleClass().contains("card-selected"))
      {
        selectedCard.getStyleClass().add("card-selected");
      }

      VBox placedCardWrapper = new VBox();
      placedCardWrapper.alignmentProperty()
          .setValue(javafx.geometry.Pos.CENTER);
      Label username = new Label(Session.getCurrentUser().getUsername());
      StackPane cardWrapper = new StackPane();

      ImageView clone = new ImageView(selectedCard.getImage());
      clone.setFitHeight(140);
      clone.setFitWidth(105);

      placedCardWrapper.getChildren().add(username);
      cardWrapper.getChildren().add(clone);
      cardWrapper.getChildren().add(getBackImageView());
      placedCardWrapper.getChildren().add(cardWrapper);

      Platform.runLater(
          () -> placedCardsWrapper.getChildren().add(placedCardWrapper));
    }
  }

  private Node getBackImageView()
  {
    Image back = new Image(getClass().getResourceAsStream("/Images/back.jpg"));
    ImageView backImageView = new ImageView(back);
    backImageView.setFitHeight(140);
    backImageView.setFitWidth(105);
    return backImageView;
  }

  public void setPlacedCardsWrapper(HBox placedCardsWrapper)
  {
    this.placedCardsWrapper = placedCardsWrapper;

  }


}
