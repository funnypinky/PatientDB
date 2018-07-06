package patientdb;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class PatientDB extends Application {

    private Stage stage;

    private BorderPane rootLayout;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        initRootPane();
        initPatientView();
        this.stage.setMinHeight(800.0);
        this.stage.setMinWidth(1200.0);
        this.stage.show();
    }

    private void initRootPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("view/MainView.fxml"));
            rootLayout = (BorderPane) loader.load();
            rootLayout.getStylesheets().add(getClass().getResource("view/ModernTheme.css").toExternalForm());
            Scene scene = new Scene(rootLayout);
            stage.setScene(scene);
        } catch (IOException e) {
        }
    }

    private void initPatientView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("view/patientView.fxml"));
            SplitPane patientOverview = (SplitPane) loader.load();
            rootLayout.setCenter(patientOverview);
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
