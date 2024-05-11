package Views.GameView;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CustomImageView extends ImageView {
    private String effortValue;

    public CustomImageView(Image image, String effortValue) {
        super(image);
        this.effortValue = effortValue;
    }

    public String getEffortValue() {
        return effortValue;
    }
}
