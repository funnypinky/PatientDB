package patientdb;

import ICD10.ICD10;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import patientdb.view.PatientViewController;

public class PatientDB extends Application {

    private Stage stage;

    private BorderPane rootLayout;

    private final DatabaseConnection connection = new DatabaseConnection();
    ICD10 icd10 = new ICD10();

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Stage is starting");
        icd10.readFile();
        System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " " + icd10.getVersion());
        this.stage = stage;
        initRootPane();
        initPatientView();
        this.stage.setMinHeight(800.0);
        this.stage.setMinWidth(1200.0);
        this.stage.show();
        this.stage.setTitle("Hyperthermie Patient Datenbank");
    }

    private void initRootPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("view/MainView.fxml"));
            rootLayout = (BorderPane) loader.load();
            //rootLayout.getStylesheets().add(getClass().getResource("view/ModernTheme.css").toExternalForm());
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().addAll(this.getClass().getResource("view/ModernTheme.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
        }
    }

    private void initPatientView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(c -> {
                return new PatientViewController(this.connection, this.icd10);
            });
            loader.setLocation(this.getClass().getResource("view/patientView.fxml"));
            SplitPane patientOverview = (SplitPane) loader.load();
            rootLayout.setCenter(patientOverview);
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) {
        System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Application started");
        launch(args);
    }

    @Override
    public void stop() {
        this.connection.closeDB();
        System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Stage is closing");
    }
}
