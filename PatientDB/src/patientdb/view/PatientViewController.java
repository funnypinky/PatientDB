/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.view;

import ICD10.ICD10;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import patientdb.DatabaseConnection;
import patientdb.data.Patient;

/**
 *
 * @author shaesler
 * @TODO:2018-08-30:Therapie-Daten einbindung in die Datenbank
 * @TODO:2018-08-31:Druckfunktion Bericht
 * @TODO:2018-08-31:ICD-10 Katalog
 * @TODO:2018-08-31:Histologie Katalog
 *
 */
public class PatientViewController implements Initializable {

    private boolean statusCreate = false;
    private Patient oldPatient;

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
    private ComboBox studyBoolean;
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
    private TreeView patientTable;
    
    private DatabaseConnection connection = null;

    private ICD10 icd10 = null;

    public PatientViewController(DatabaseConnection con, ICD10 icd10) {
        this.connection = con;
        this.icd10 = icd10;
    }

    /**
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sexBox.getItems().addAll("Weiblich", "Männlich", "Unbestimmt");
        sexBox.setValue("Unbestimmt");
        studyBoolean.getItems().addAll("ja","nein","ausserhalb");
        studyBoolean.setValue("nein");
        TreeItem rootItem = new TreeItem<>(new Patient());
        patientTable.setRoot(rootItem);
        patientTable.setShowRoot(false);
        if (connection != null) {
            connection.getPatientList().stream().forEach((patient) -> {
                patientTable.getRoot().getChildren().add(new TreeItem<>(patient));
                
            });
        }
        tumorTF.getItems().addAll(icd10.getItems());
        new AutoCompleteComboBoxListener(tumorTF);
        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                getPatientFromList(null);
                changePatientBt.setDisable(false);
                deletePatientBt.setDisable(false);
            } else {
                changePatientBt.setDisable(true);
                deletePatientBt.setDisable(true);
                clearMask();
            }
        });
        deathTF.valueProperty().addListener((ov, oldValue, newValue) -> {
            testDeath(null);
        });
    }

    @FXML
    public void newPatient(ActionEvent event) {
        //Set another Button disable
        changePatientBt.setDisable(true);
        deletePatientBt.setDisable(true);
        saveNewPatient.setVisible(!saveNewPatient.isVisible());
        abortNewPatient.setVisible(!abortNewPatient.isVisible());

        this.statusCreate = true;
        //Enable Edit
        changeEditStatus(true);
        clearMask();
    }

    @FXML
    public void abortPatient(ActionEvent event) {
        //Set another Button disable
        changePatientBt.setDisable(true);
        deletePatientBt.setDisable(true);
        saveNewPatient.setVisible(!saveNewPatient.isVisible());
        abortNewPatient.setVisible(!abortNewPatient.isVisible());

        changeEditStatus(false);
        clearMask();
        getPatientFromList(null);
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
                deathTF.setValue(null);
            }
        }
    }

    @FXML
    public void createNewPatient(ActionEvent event) {
        Patient patient = new Patient();
        patient.setUniqueID(Math.round(Math.random() * 1000));
        patient.setAriaID(ariaIDTF.getText());
        patient.setFirstName(firstNameTF.getText());
        patient.setLastName(lastNameTF.getText());
        patient.setBirthday(birthdayTF.getValue());
        patient.setDeathDay(deathTF.getValue());
        patient.setSex(sexBox.getValue().toString());
        if (this.statusCreate) {
            if (this.connection.addPatient(patient)) {
                //patientTable.getItems().add(patient);
                abortPatient(event);
                this.statusCreate = false;
            }
        } else {
            if (this.connection.updatePatient(patient, oldPatient.getAriaID())) {
                abortPatient(event);
                this.statusCreate = false;
            }
        }
    }

    @FXML
    public void getPatientFromList(ActionEvent event) {
        TreeItem selectedItem = (TreeItem) patientTable.getSelectionModel().getSelectedItem();
        Patient selectPatient = (Patient) selectedItem.getValue();
        if (selectPatient != null) {
            ariaIDTF.setText(selectPatient.getAriaID());
            firstNameTF.setText(selectPatient.getFirstName());
            lastNameTF.setText(selectPatient.getLastName());
            birthdayTF.setValue(selectPatient.getBirthday());
            deathTF.setValue(selectPatient.getDeathDay());
            sexBox.setValue(selectPatient.getSex());
        }
    }

    @FXML
    public void deletePatient(ActionEvent event) {
        if (connection.deletePatient((Patient) patientTable.getSelectionModel().getSelectedItem())) {
            connection.getPatientList().stream().forEach((patient) -> {
                patientTable.getRoot().getChildren().add(new TreeItem<>(patient));
            });
        }
    }

    @FXML
    public void changePatient(ActionEvent event) {
        TreeItem selectedItem = (TreeItem) patientTable.getSelectionModel().getSelectedItem();
        this.oldPatient = (Patient) selectedItem.getValue();
        changeEditStatus(true);
        sexBox.setValue(oldPatient.getSex());
        saveNewPatient.setVisible(!saveNewPatient.isVisible());
        abortNewPatient.setVisible(!abortNewPatient.isVisible());
    }

    private void changeEditStatus(boolean status) {
        //Enable Edit
        definitivBoolean.setDisable(!status);
        preopBoolean.setDisable(!status);
        primaryBoolean.setDisable(!status);
        rezidivBoolean.setDisable(!status);
        simCTBoolean.setDisable(!status);
        simRTBoolean.setDisable(!status);
        studyBoolean.setDisable(!status);

        birthdayTF.setDisable(!status);
        birthdayTF.setEditable(status);
        deathTF.setDisable(!status);
        deathTF.setEditable(status);
        firstHTDate.setDisable(!status);
        firstHTDate.setEditable(status);

        commentTA.setDisable(!status);
        compilikationTA.setDisable(!status);

        ariaIDTF.setDisable(!status);
        compSessionTF.setDisable(!status);
        firstNameTF.setDisable(!status);
        plannedSessionTF.setDisable(!status);

        sizeTF.setDisable(!status);
        lastNameTF.setDisable(!status);

        sexBox.setEditable(status);
        sexBox.setDisable(!status);
    }

    private void clearMask() {
        birthdayTF.setValue(null);
        deathTF.setValue(null);
        firstHTDate.setValue(null);
        commentTA.clear();
        compilikationTA.clear();
        ariaIDTF.clear();
        compSessionTF.clear();
        firstNameTF.clear();
        plannedSessionTF.clear();
        sizeTF.clear();
        lastNameTF.clear();
        sexBox.setValue("Unbestimmt");
    }

    public DatabaseConnection getConnection() {
        return connection;
    }

    public void setConnection(DatabaseConnection connection) {
        this.connection = connection;
        connection.getPatientList().stream().forEach((patient) -> {
            patientTable.getRoot().getChildren().add(new TreeItem<>(patient));
        });
    }

}
