/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

/**
 *
 * @author shaesler
 */
public class FilterController implements Initializable {

    @FXML
    private Button createReport;

    @FXML
    private Button cancelReport;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    private final MainViewController parentControl;

    public FilterController(MainViewController parentControl) {
        this.parentControl = parentControl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.startDate.setValue(LocalDate.of(LocalDate.now().getYear(), 1, 1));
        this.endDate.setValue(LocalDate.of(LocalDate.now().getYear(), 12, 31));
    }

    @FXML
    public void cancelDialog(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }

    @FXML
    public void createDialog(ActionEvent event) {
        cancelDialog(event);
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", startDate.getValue());
        parameters.put("endDate", endDate.getValue());
        this.parentControl.getPrintJob().print(parameters);
    }

}
