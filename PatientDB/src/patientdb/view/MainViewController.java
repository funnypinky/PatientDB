/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.view;

import ICD.ICDCode;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import patientdb.DatabaseConnection;

/**
 *
 * @author shaesler
 */
public class MainViewController implements Initializable {

    @FXML
    private MenuItem printReport;

    @FXML
    private MenuItem exitButton;
    
    @FXML
    private MenuBar myMenuBar;

    Dialog aboutDialog = new Dialog();

    private DatabaseConnection connection = null;

    private ICDCode icd10 = null;
    private ICDCode icd3 = null;
    private ICDCode mCode = null;

    private Print printJob;

    public MainViewController(DatabaseConnection connection) {
        this.connection = connection;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        printJob = new Print(this.connection);
        aboutDialog.initStyle(StageStyle.UNDECORATED);
    }

    @FXML
    public void showFilterDialog(ActionEvent event) throws IOException {
        this.printJob.setOwner((Stage) myMenuBar.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(c -> {
            return new FilterController(this);
        });
        loader.setLocation(this.getClass().getResource("filterDialog.fxml"));
        Node content = loader.load();
        DialogPane pane = aboutDialog.getDialogPane();

        pane.setContent(content);
        aboutDialog.show();
    }

    public Dialog getAboutDialog() {
        return aboutDialog;
    }

    public Print getPrintJob() {
        return printJob;
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }
}
