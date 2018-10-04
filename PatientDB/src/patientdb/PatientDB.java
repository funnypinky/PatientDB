package patientdb;

import ICD.ICDCode;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Preloader.StateChangeNotification;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import patientdb.preloader.CustomPreloadNotification;
import patientdb.view.MainViewController;
import patientdb.view.PatientViewController;

public class PatientDB extends Application {

    private Stage stage;

    private BorderPane rootLayout;


    ICDCode icd10 = new ICDCode(System.getProperty("user.dir") + "\\data\\ICD10", "[A-Z][0-9]*[.][0-9]");
    ICDCode icd3 = new ICDCode(System.getProperty("user.dir") + "\\data\\ICD3", "[A-Z][0-9]*[.][0-9]");
    ICDCode mCode = new ICDCode(System.getProperty("user.dir") + "\\data\\ICD3", "[0-9]*[:][0-9]*");
    private DatabaseConnection connection;

    BooleanProperty ready = new SimpleBooleanProperty(false);

    @Override
    public void init() throws Exception {
        notifyPreloader(new CustomPreloadNotification(1.0/5.0, "...lade ICD-10 Katalog-Datei"));
        icd10.readFile();
        notifyPreloader(new CustomPreloadNotification(2.0/5.0, "...lade ICD-3 Katalog-Datei"));
        icd3.readFile();
        notifyPreloader(new CustomPreloadNotification(3.0/5.0, "...lade Histologie Katalog-Datei"));
        mCode.readFile();
        notifyPreloader(new CustomPreloadNotification(4.0/5.0, "...lade Histologie Katalog-Datei"));
        for (int i = 0; i < mCode.getItems().size(); i++) {
            mCode.getItems().get(i).setCode("M" + mCode.getItems().get(i).getCode());
        }
        notifyPreloader(new CustomPreloadNotification(5.0/5.0, "...stelle Verbindung zur Datenbank her"));
        connection = new DatabaseConnection(this.icd10, this.icd3, this.mCode);
        ready.setValue(Boolean.TRUE);

        notifyPreloader(new StateChangeNotification(
                StateChangeNotification.Type.BEFORE_START));
                
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Stage is starting");
        System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " " + icd10.getVersion());
        this.stage = stage;
        initRootPane();
        initPatientView();
        this.stage.setMinHeight(900.0);
        this.stage.setMinWidth(1200.0);
        this.stage.setTitle("Hyperthermie Patient Datenbank");
        this.stage.getIcons().add(new Image(this.getClass().getResourceAsStream("view/images/favicon.png")));
        this.stage.show();
    }

    private void initRootPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(c -> {
                return new MainViewController(this.connection);
            });
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
            VBox patientOverview = (VBox) loader.load();
            rootLayout.setCenter(patientOverview);
        } catch (IOException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void stop() {
        this.connection.closeDB();
        System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Stage is closing");
    }

}
