package Views.GameView;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class CustomImageViewAdapter extends ImageView implements CustomImageView {
    private String effortValue;

    public CustomImageViewAdapter(Image image, String effortValue) {
        super(image);
        this.effortValue = effortValue;
    }

    @Override
    public String getEffortValue() {
        return effortValue;
    }

}
