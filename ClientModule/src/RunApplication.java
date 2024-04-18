import Application.ClientFactory;
import Application.ModelFactory;
import Application.ViewFactory;
import Application.Session;
import DataTypes.PlanningPoker;
import DataTypes.User;
import javafx.application.Application;
import javafx.stage.Stage;

public class RunApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientFactory clientFactory = ClientFactory.getInstance();
        ViewFactory viewFactory = ViewFactory.getInstance();

        clientFactory.getClient();
        viewFactory.loadLoginView();

    }

}
