package Views.GameView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class CountDown {
    private final int START_COUNTDOWN = 3;
    private final int FINAL_COUNTDOWN = 0;
    @FXML
    private StackPane countDownCircle;

    public CountDown(StackPane countDownCircle) {
        this.countDownCircle = countDownCircle;
    }

    public void startCountDown() {
        countDownCircle.setVisible(true);
        Label countDownNumber = (Label) countDownCircle.getChildren().get(1);
        for (int i = START_COUNTDOWN; i >= FINAL_COUNTDOWN; i--) {
            countDownNumber.setText(String.valueOf(i));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        countDownCircle.setVisible(false);
    }
}
