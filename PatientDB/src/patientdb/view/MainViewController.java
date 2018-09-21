/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.view;

import ICD.ICDCode;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import patientdb.DatabaseConnection;

/**
 *
 * @author shaesler
 */
public class MainViewController implements Initializable {

    @FXML
    private MenuItem printReport;

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
    }

    @FXML
    public void print(ActionEvent event) {
        printJob.print();
    }
}
