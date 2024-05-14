package Views.GameView;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.List;

public interface CustomImageView {

        String getEffortValue();
        void setFitHeight(double height);
        void setFitWidth(double width);
        void setTranslateX(double x);
        void setOnMouseEntered(EventHandler<? super MouseEvent> handler);
        void setOnMouseExited(EventHandler<? super MouseEvent> handler);
        void setOnMouseClicked(EventHandler<? super MouseEvent> handler);
        ObservableList<String> getStyleClass();
}
