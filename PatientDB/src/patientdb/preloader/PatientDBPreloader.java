/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.preloader;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Simple Preloader Using the ProgressBar Control
 *
 * @author shaesler
 */
public class PatientDBPreloader extends Preloader implements Initializable {

    private Stage preloaderStage;
    
     private Parent root;

    @FXML
    private ProgressBar statusBar;
    
    @FXML
    private Label statusLabel;
      
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Application started");
        this.preloaderStage = primaryStage;
        root = FXMLLoader.load(this.getClass().getResource("preLoader.fxml"));
        String css = this.getClass().getResource("preload.css").toExternalForm();


        root.getChildrenUnmodifiable().stream().filter((childrenUnmodifiable) -> (childrenUnmodifiable instanceof ProgressBar)).forEachOrdered((childrenUnmodifiable) -> {
            this.statusBar = (ProgressBar) childrenUnmodifiable;
        });
        root.getChildrenUnmodifiable().stream().filter((childrenUnmodifiable) -> (childrenUnmodifiable instanceof Label)).forEachOrdered((childrenUnmodifiable) -> {
            this.statusLabel = (Label) childrenUnmodifiable;
        });
        statusBar.setProgress(0);
        statusLabel.setText("...lade Application");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(css);
        this.preloaderStage.setScene(scene);
        this.preloaderStage.initStyle(StageStyle.UNDECORATED);
        this.preloaderStage.setTitle("Lade Anwendung...");

        this.preloaderStage.show();

    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
       if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
        if (preloaderStage.isShowing()) {
            //fade out, hide stage at the end of animation
            FadeTransition ft = new FadeTransition(
                Duration.millis(1000), preloaderStage.getScene().getRoot());
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                final Stage s = preloaderStage;
                EventHandler<ActionEvent> eh = (ActionEvent t) -> {
                    s.hide();
            };
                ft.setOnFinished(eh);
                ft.play();
        } else {
            preloaderStage.hide();
        }
    }

    }

    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        //application loading progress is rescaled to be first 50%
        //Even if there is nothing to load 0% and 100% events can be
        // delivered
        if (pn.getProgress() != 1.0) {
          statusBar.setProgress(pn.getProgress()/2);
        }
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        if (pn instanceof CustomPreloadNotification) {
           //expect application to send us progress notifications 
           //with progress ranging from 0 to 1.0
           double v = ((CustomPreloadNotification) pn).getProgress();
           statusBar.setProgress(v);    
           statusLabel.setText(((CustomPreloadNotification) pn).getDetails());
        } 
    }  

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Application initialized");
    }

}
