import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ObservableList<TransactionModel> transactionModels = FXCollections.observableArrayList();
        FTTController controller = new FTTController(transactionModels, primaryStage);

        primaryStage.setTitle("Family Finance Tracker");
        primaryStage.setScene(controller.createScene());
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}