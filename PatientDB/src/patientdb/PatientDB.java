package patientdb;

import ICD.ICDCode;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    ICDCode icd10 = new ICDCode("data\\ICD10", "[A-Z][0-9]*[.][0-9]");
    ICDCode icd3 = new ICDCode("data\\ICD3", "[A-Z][0-9]*[.][0-9]");
    ICDCode mCode = new ICDCode("data\\ICD3", "[0-9]*[:][0-9]*");
    private DatabaseConnection connection;

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Stage is starting");
        icd10.readFile();
        icd3.readFile();
        mCode.readFile();
        for (int i = 0; i < mCode.getItems().size(); i++) {
            mCode.getItems().get(i).setCode("M" + mCode.getItems().get(i).getCode());
        }
        connection = new DatabaseConnection(this.icd10, this.icd3, this.mCode);
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
                return new PatientViewController(this.connection, this.icd10, this.icd3, this.mCode);
            });
            loader.setLocation(this.getClass().getResource("view/patientView.fxml"));
            SplitPane patientOverview = (SplitPane) loader.load();
            rootLayout.setCenter(patientOverview);
        } catch (IOException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
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
