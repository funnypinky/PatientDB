/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import patientdb.data.Patient;

/**
 *
 * @author shaesler
 */
public class PatientViewController implements Initializable {

    @FXML
    private Button newPatientBt;
    @FXML
    private Button changePatientBt;
    @FXML
    private Button deletePatientBt;
    @FXML
    private CheckBox definitivBoolean;
    @FXML
    private CheckBox preopBoolean;
    @FXML
    private CheckBox primaryBoolean;
    @FXML
    private CheckBox rezidivBoolean;
    @FXML
    private CheckBox simCTBoolean;
    @FXML
    private CheckBox simRTBoolean;
    @FXML
    private CheckBox studyBoolean;
    @FXML
    private ComboBox gradTF;
    @FXML
    private ComboBox histoTF;
    @FXML
    private ComboBox stadiumTF;
    @FXML
    private ComboBox tumorTF;
    @FXML
    private DatePicker birthdayTF;
    @FXML
    private DatePicker firstHTDate;
    @FXML
    private TextArea commentTA;
    @FXML
    private TextArea compilikationTA;
    @FXML
    private TextField ariaIDTF;
    @FXML
    private TextField compSessionTF;
    @FXML
    private TextField lastNameTF;
    @FXML
    private TextField plannedSessionTF;
    @FXML
    private TextField sizeTF;
    @FXML
    private TextField firstNameTF;
    @FXML
    private DatePicker deathTF;
    @FXML
    private ComboBox sexBox;
    @FXML
    private Button abortNewPatient;
    @FXML
    private Button saveNewPatient;
    @FXML
    private TableView patientTable;
    @FXML
    private TableColumn idColumn;
    @FXML
    private TableColumn lastNameColumn;
    @FXML
    private TableColumn firstNameColumn;
    @FXML
    private TableColumn birthdayColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sexBox.getItems().addAll("Weiblich", "Männlich", "Unbestimmt");
        sexBox.setValue("Unbestimmt");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ariaID"));
        birthdayColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                getPatientFromList(null);
            }
        }
        );
    }

    @FXML
    public void newPatient(ActionEvent event) {
        //Set another Button disable
        changePatientBt.setDisable(true);
        deletePatientBt.setDisable(true);

        //Enable Edit
        definitivBoolean.setDisable(!definitivBoolean.isDisable());
        preopBoolean.setDisable(!preopBoolean.isDisable());
        primaryBoolean.setDisable(!primaryBoolean.isDisable());
        rezidivBoolean.setDisable(!rezidivBoolean.isDisable());
        simCTBoolean.setDisable(!simCTBoolean.isDisable());
        simRTBoolean.setDisable(!simRTBoolean.isDisable());
        studyBoolean.setDisable(!studyBoolean.isDisable());

        birthdayTF.setDisable(!birthdayTF.isDisable());
        deathTF.setDisable(!deathTF.isDisable());
        firstHTDate.setDisable(!firstHTDate.isDisable());

        commentTA.setEditable(!commentTA.isEditable());
        compilikationTA.setEditable(!compilikationTA.isEditable());

        ariaIDTF.setEditable(!ariaIDTF.isEditable());
        compSessionTF.setEditable(!compSessionTF.isEditable());
        lastNameTF.setEditable(!lastNameTF.isEditable());
        plannedSessionTF.setEditable(!plannedSessionTF.isEditable());
        sizeTF.setEditable(!sizeTF.isEditable());
        firstNameTF.setEditable(!firstNameTF.isEditable());

        sexBox.setDisable(!sexBox.isDisable());

        saveNewPatient.setVisible(!saveNewPatient.isVisible());
        abortNewPatient.setVisible(!abortNewPatient.isVisible());
    }

    @FXML
    public void abortPatient(ActionEvent event) {
        //Set another Button disable
        changePatientBt.setDisable(false);
        deletePatientBt.setDisable(false);

        //Enable Edit
        definitivBoolean.setDisable(!definitivBoolean.isDisable());
        definitivBoolean.setSelected(false);
        preopBoolean.setDisable(!preopBoolean.isDisable());
        preopBoolean.setSelected(false);
        primaryBoolean.setDisable(!primaryBoolean.isDisable());
        primaryBoolean.setSelected(false);
        rezidivBoolean.setDisable(!rezidivBoolean.isDisable());
        rezidivBoolean.setSelected(false);
        simCTBoolean.setDisable(!simCTBoolean.isDisable());
        simCTBoolean.setSelected(false);
        simRTBoolean.setDisable(!simRTBoolean.isDisable());
        simRTBoolean.setSelected(false);
        studyBoolean.setDisable(!studyBoolean.isDisable());
        studyBoolean.setSelected(false);

        birthdayTF.setDisable(!birthdayTF.isDisable());
        birthdayTF.setValue(null);
        deathTF.setDisable(!deathTF.isDisable());
        deathTF.setValue(null);
        firstHTDate.setDisable(!firstHTDate.isDisable());
        firstHTDate.setValue(null);

        commentTA.setEditable(!commentTA.isEditable());
        commentTA.clear();
        compilikationTA.setEditable(!compilikationTA.isEditable());
        compilikationTA.clear();

        ariaIDTF.setEditable(!ariaIDTF.isEditable());
        ariaIDTF.clear();
        compSessionTF.setEditable(!compSessionTF.isEditable());
        compSessionTF.clear();
        firstNameTF.setEditable(!firstNameTF.isEditable());
        firstNameTF.clear();
        plannedSessionTF.setEditable(!plannedSessionTF.isEditable());
        plannedSessionTF.clear();
        sizeTF.setEditable(!sizeTF.isEditable());
        sizeTF.clear();
        lastNameTF.setEditable(!lastNameTF.isEditable());
        lastNameTF.clear();

        sexBox.setDisable(!sexBox.isDisable());

        saveNewPatient.setVisible(!saveNewPatient.isVisible());
        abortNewPatient.setVisible(!abortNewPatient.isVisible());

        sexBox.setValue("Unbestimmt");
    }

    @FXML
    public void testDeath(ActionEvent event) {
        LocalDate today = LocalDate.now();
        if (deathTF.getValue() != null) {
            if (deathTF.getValue().isAfter(today)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler - Tötungsabsicht");
                alert.setHeaderText("Tötungsabsicht");
                alert.setContentText("Sie haben ein Totesdatum nach dem heutigen Datum eingeben, "
                        + "aber Tötungsabsichten können nicht berücksichtig werden."
                        + "\n Bitte korrigieren Sie den Fehler.");
                alert.showAndWait();
            }
        }
    }
    @FXML
    public void createNewPatient(ActionEvent event) {
        Patient patient = new Patient();
        patient.setUniqueID(Math.round(Math.random()*1000));
        patient.setAriaID(ariaIDTF.getText());
        patient.setFirstName(firstNameTF.getText());
        patient.setLastName(lastNameTF.getText());
        patient.setBirthday(birthdayTF.getValue());
        patient.setDeathDay(deathTF.getValue());
        patient.setSex(sexBox.getValue().toString());
        patientTable.getItems().add(patient);
        
        abortPatient(event);
    }
    
    @FXML
    public void getPatientFromList(ActionEvent event) {
        Patient selectPatient = (Patient)patientTable.getSelectionModel().getSelectedItem();
        ariaIDTF.setText(selectPatient.getAriaID());
        firstNameTF.setText(selectPatient.getFirstName());
        lastNameTF.setText(selectPatient.getLastName());
        birthdayTF.setValue(selectPatient.getBirthday());
        deathTF.setValue(selectPatient.getDeathDay());
        sexBox.setValue(selectPatient.getSex());
    }
}
