/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author user
 */
public class PatientDB extends Application {

    private Stage stage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        initRootPane();
        initPatientView();
    }

    private void initRootPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("view/MainView.fxml"));

            rootLayout = (BorderPane) loader.load();
            rootLayout.getStylesheets().add(getClass().getResource("view/ModernTheme.css").toExternalForm());

            Scene scene = new Scene(rootLayout);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
        }

    }

    private void initPatientView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("view/patientView.fxml"));
            SplitPane patientOverview = (SplitPane) loader.load();
            // Set person overview into the center of root layout.

            rootLayout.setCenter(patientOverview);
            //patientOverview.autosize();

        } catch (IOException e) {
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
