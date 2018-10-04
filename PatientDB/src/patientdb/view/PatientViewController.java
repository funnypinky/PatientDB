/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.view;

import ICD.ICDCode;
import ICD.ICDModel;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import patientdb.DatabaseConnection;
import patientdb.data.Diagnosis;
import patientdb.data.Patient;
import patientdb.data.Series;
import patientdb.data.Staging;

/**
 *
 * @author shaesler
 * @TODO:2018-08-30:Therapie-Daten einbindung in die Datenbank
 * @TODO:2018-08-31:Druckfunktion Bericht
 * @TODO:2018-08-31:ICD-10 Katalog
 * @TODO:2018-08-31:Histologie Katalog
 * @TODO:2018-09-07:Test ob erforderliche Eingaben gemacht wurden
 * @TODO:2018-09-14:Test ob PatientID doppelt vergeben wird
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
    private ComboBox studyTF;
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
    @FXML
    private ComboBox icdoTF;
    @FXML
    private ComboBox lokalTF;
    @FXML
    private Label studyLabel;
    @FXML
    private TextField studyNameTF;
    @FXML
    private TextField caseTF;
    @FXML
    private DatePicker outDay;
    @FXML
    private DatePicker inDay;
    @FXML
    private Button addSession;
    @FXML
    private Button saveSession;
    @FXML
    private Button abortSession;
    @FXML
    private TextField filterLastName;
    @FXML
    private TextField filterFirstName;
    @FXML
    private TextField filterPatientNumber;
    @FXML
    private Label filterSummary;
    
    private DatabaseConnection connection = null;
    
    private ICDCode icd10 = null;
    private ICDCode icd3 = null;
    private ICDCode mCode = null;
    
    private final Image nodeImage = new Image(getClass().getResourceAsStream("images/human.png"), 18, 18, false, true);
    private final Image newPatientImage = new Image(getClass().getResourceAsStream("images/newPatient.png"), 18, 18, false, true);
    private final Image deletePatientImage = new Image(getClass().getResourceAsStream("images/deletePatient.png"), 18, 18, false, true);
    
    public PatientViewController(DatabaseConnection con, ICDCode icd10, ICDCode icd3, ICDCode mCode) {
        this.connection = con;
        this.icd10 = icd10;
        this.icd3 = icd3;
        this.mCode = mCode;
    }

    /**
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem rootItem = new TreeItem<>(new Patient());
        patientTable.setRoot(rootItem);
        patientTable.setShowRoot(false);

//add Listener
        new AutoCompleteComboBoxListener(tumorTF);
        new AutoCompleteComboBoxListener(histoTF);
        new AutoCompleteComboBoxListener(icdoTF);
        
        UnaryOperator<Change> rejectChange = (Change c) -> {
            // check if the change might effect the validating predicate
            if (c.isContentChange() && !ariaIDTF.isDisable()) {
                // check if change is valid
                ArrayList tempList = (ArrayList) connection.getPatientList();
                if (tempList != null && !c.getControlNewText().isEmpty()) {
                    
                    if (patientDouble(tempList, c.getControlNewText())) {
                        final ContextMenu menu = new ContextMenu();
                        menu.getStyleClass().add("warning");
                        menu.getItems().add(new MenuItem("Der Eintrag ist doppelt vorhanden!"));
                        menu.show(c.getControl(), Side.BOTTOM, 0, 0);
                        // return null to reject the change
                        return null;
                    }
                }
            }
            // valid change: accept the change by returning it
            return c;
        };
        ariaIDTF.setTextFormatter(new TextFormatter(rejectChange));
        
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
        
        studyTF.valueProperty().addListener((ov, oldValue, newValue) -> {
            showStudyName(null);
        });
        ariaIDTF.textProperty().addListener((ov, oldValue, newValue) -> {
            checkRequired(null);
            if (ariaIDTF.getText().isEmpty() && !ariaIDTF.isDisable()) {
                ariaIDTF.getStyleClass().add("test");
            } else {
                ariaIDTF.getStyleClass().remove("test");
            }
        });
        ariaIDTF.disableProperty().addListener((ov, oldValue, newValue) -> {
            changeCSS(ariaIDTF, !ariaIDTF.isDisable() && ariaIDTF.getText().isEmpty(), "test");
        });
        firstNameTF.textProperty().addListener((ov, oldValue, newValue) -> {
            checkRequired(null);
            if (firstNameTF.getText().isEmpty() && !firstNameTF.isDisable()) {
                firstNameTF.getStyleClass().add("test");
            } else {
                firstNameTF.getStyleClass().remove("test");
            }
        });
        firstNameTF.disableProperty().addListener((ov, oldValue, newValue) -> {
            changeCSS(firstNameTF, !firstNameTF.isDisable() && firstNameTF.getText().isEmpty(), "test");
        });
        lastNameTF.textProperty().addListener((ov, oldValue, newValue) -> {
            checkRequired(null);
            if (lastNameTF.getText().isEmpty() && !lastNameTF.isDisable()) {
                lastNameTF.getStyleClass().add("test");
            } else {
                lastNameTF.getStyleClass().remove("test");
            }
        });
        lastNameTF.disableProperty().addListener((ov, oldValue, newValue) -> {
            changeCSS(lastNameTF, !lastNameTF.isDisable() && lastNameTF.getText().isEmpty(), "test");
        });
        tumorTF.valueProperty().addListener((ov, oldValue, newValue) -> {
            checkRequired(null);
            if (tumorTF.getValue() != null && !tumorTF.isDisable()) {
                tumorTF.getStyleClass().add("test");
            } else {
                tumorTF.getStyleClass().remove("test");
            }
        });
        tumorTF.disableProperty().addListener((ov, oldValue, newValue) -> {
            changeCSS(tumorTF, !tumorTF.isDisable() && tumorTF.getValue() == null, "test");
        });
        filterFirstName.textProperty().addListener((ov, oldValue, newValue) -> {
            filterList(new ActionEvent());
        });
        filterLastName.textProperty().addListener((ov, oldValue, newValue) -> {
            filterList(new ActionEvent());
        });
        filterPatientNumber.textProperty().addListener((ov, oldValue, newValue) -> {
            filterList(new ActionEvent());
        });
        newPatientBt.setGraphic(new ImageView(newPatientImage));
        deletePatientBt.setGraphic(new ImageView(deletePatientImage));
        
//fill elements
        updateList();
        tumorTF.getItems().addAll(icd10.getItems());
        histoTF.getItems().addAll(mCode.getItems());
        icdoTF.getItems().addAll(icd3.getItems());
        sexBox.getItems().addAll("Weiblich", "Männlich", "Unbestimmt");
        sexBox.setValue("Unbestimmt");
        studyTF.getItems().addAll("ja", "nein", "ausserhalb");
        studyTF.setValue("nein");
        lokalTF.getItems().addAll("links", "rechts", "beidseits", "Mittellinie");
        gradTF.getItems().addAll("G1 gut differenziert", "G2 mäßig differenziert", "G3 schlecht differenziert", "G4 undifferenziert", "GX nicht bestimmbar");
        
    }
    
    @FXML
    public void newPatient(ActionEvent event) {
        //Set another Button disable
        changePatientBt.setDisable(true);
        deletePatientBt.setDisable(true);
        saveNewPatient.setVisible(true);
        abortNewPatient.setVisible(true);
        patientTable.getSelectionModel().clearSelection();
        
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
    public void checkRequired(ActionEvent e) {
        if (!ariaIDTF.getText().isEmpty()) {
            if (!firstNameTF.getText().isEmpty()) {
                if (!lastNameTF.getText().isEmpty()) {
                    if (tumorTF.getValue() != null) {
                        saveNewPatient.setDisable(false);
                    }
                }
            }
        }
    }
    
    @FXML
    public void addSessionAction(ActionEvent event) {
        changeStatusSessionPanel(true);
        saveSession.setVisible(true);
        abortSession.setVisible(true);
        
        newPatientBt.setDisable(true);
        changePatientBt.setDisable(true);
        deletePatientBt.setDisable(true);
        
    }
    
    public void saveSession(ActionEvent event) {
        Patient selectPatient;
        TreeItem selectedItem = (TreeItem) patientTable.getSelectionModel().getSelectedItem();
        if (selectedItem.getValue().getClass() != Patient.class) {
            selectPatient = (Patient) selectedItem.getParent().getValue();
        } else {
            selectPatient = (Patient) selectedItem.getValue();
        }
        selectPatient.setSeries(new ArrayList<>());
        selectPatient.getSeries().add(new Series());
        int lastIndex = selectPatient.getSeries().size() - 1;
        selectPatient.getSeries().get(lastIndex).setSapNumber(caseTF.getText());
        selectPatient.getSeries().get(lastIndex).setTherapyDate(this.firstHTDate.getValue());
        selectPatient.getSeries().get(lastIndex).setInDay(this.inDay.getValue());
        selectPatient.getSeries().get(lastIndex).setOutDay(this.outDay.getValue());
        selectPatient.getSeries().get(lastIndex).setSimCT(this.simCTBoolean.isSelected());
        selectPatient.getSeries().get(lastIndex).setSimRT(this.simRTBoolean.isSelected());
        selectPatient.getSeries().get(lastIndex).setComplication(this.compilikationTA.getText());
        selectPatient.getSeries().get(lastIndex).setComments(this.commentTA.getText());
        if (selectedItem.getValue() instanceof Patient) {
            selectedItem.getChildren().add(new TreeItem(selectPatient.getSeries().get(lastIndex)));
        } else {
            selectedItem.getParent().getChildren().add(new TreeItem(selectPatient.getSeries().get(lastIndex)));
            
        }
        connection.sqlInsertSession(selectPatient.getAriaID(), selectPatient.getSeries().get(lastIndex));
        abortSessionAction(event);
    }
    
    @FXML
    public void abortSessionAction(ActionEvent event) {
        changeStatusSessionPanel(false);
        saveSession.setVisible(false);
        abortSession.setVisible(false);
        
        newPatientBt.setDisable(false);
        changePatientBt.setDisable(false);
        deletePatientBt.setDisable(false);
        
        caseTF.clear();
    }
    
    @FXML
    public void createNewPatient(ActionEvent event) {
        Patient patient = new Patient();
        patient.setDiagnoses(new Diagnosis());
        patient.getDiagnose().setStaging(new Staging());
        
        patient.setAriaID(ariaIDTF.getText());
        patient.setFirstName(firstNameTF.getText());
        patient.setLastName(lastNameTF.getText());
        patient.setBirthday(birthdayTF.getValue());
        patient.setDeathDay(deathTF.getValue());
        patient.setSex(sexBox.getValue().toString());
        patient.setStudy(studyTF.getValue().toString());
        patient.setStudyName(studyNameTF.getText());
        patient.setPretherapy(preopBoolean.isSelected());
        
        if (tumorTF.getSelectionModel().getSelectedIndex() != -1) {
            patient.getDiagnose().setICD10((ICDModel) tumorTF.getItems().get(tumorTF.getSelectionModel().getSelectedIndex()));
        }
        patient.getDiagnose().setPrimary(primaryBoolean.isSelected());
        patient.getDiagnose().setRezidiv(rezidivBoolean.isSelected());
        patient.getDiagnose().setPreop(preopBoolean.isSelected());
        
        if (histoTF.getSelectionModel().getSelectedIndex() != -1) {
            patient.getDiagnose().getStaging().setmCode((ICDModel) histoTF.getItems().get(histoTF.getSelectionModel().getSelectedIndex()));
        }
        if (gradTF.getSelectionModel().getSelectedIndex() != -1) {
            patient.getDiagnose().getStaging().setGrading(gradTF.getItems().get(gradTF.getSelectionModel().getSelectedIndex()).toString());
        }
        if (lokalTF.getSelectionModel().getSelectedIndex() != -1) {
            patient.getDiagnose().getStaging().setLokal(lokalTF.getItems().get(lokalTF.getSelectionModel().getSelectedIndex()).toString());
        }
        patient.getDiagnose().getStaging().setSize(sizeTF.getText());
        
        if (this.statusCreate) {
            this.connection.updatePatient(patient, patient.getAriaID());
            patientTable.getRoot().getChildren().add(patient);
            abortPatient(event);
            this.statusCreate = false;
        } else {
            this.connection.updatePatient(patient, oldPatient.getAriaID());
            abortPatient(event);
            this.statusCreate = false;
        }
        updateList();
    }
    
    @FXML
    public void getPatientFromList(ActionEvent event) {
        clearMask();
        addSession.setDisable(false);
        abortSessionAction(event);
        TreeItem selectedItem = (TreeItem) patientTable.getSelectionModel().getSelectedItem();
        Patient selectPatient;
        Series selectSeries = null;
        if (selectedItem != null) {
            if (selectedItem.getValue() instanceof Patient) {
                selectPatient = (Patient) selectedItem.getValue();
            } else {
                selectPatient = (Patient) selectedItem.getParent().getValue();
                selectSeries = (Series) selectedItem.getValue();
            }
            
            if (selectPatient != null) {
                ariaIDTF.setText(selectPatient.getAriaID());
                firstNameTF.setText(selectPatient.getFirstName());
                lastNameTF.setText(selectPatient.getLastName());
                birthdayTF.setValue(selectPatient.getBirthday());
                deathTF.setValue(selectPatient.getDeathDay());
                sexBox.setValue(selectPatient.getSex());
                studyTF.setValue(selectPatient.getStudy());
                studyNameTF.setText(selectPatient.getStudyName());
                preopBoolean.setSelected(selectPatient.getPretherapy());
                
                if (selectPatient.getDiagnose() != null) {
                    tumorTF.setValue(selectPatient.getDiagnose().getICD10());
                    primaryBoolean.setSelected(selectPatient.getDiagnose().isPrimary());
                    rezidivBoolean.setSelected(selectPatient.getDiagnose().isRezidiv());
                    preopBoolean.setSelected(selectPatient.getDiagnose().isPreop());
                }
                
                if (selectPatient.getDiagnose().getStaging() != null) {
                    histoTF.setValue(selectPatient.getDiagnose().getStaging().getmCode());
                    gradTF.setValue(selectPatient.getDiagnose().getStaging().getGrading());
                    sizeTF.setText(selectPatient.getDiagnose().getStaging().getSize());
                    lokalTF.setValue(selectPatient.getDiagnose().getStaging().getLokal());
                }
            }
            if (selectSeries != null) {
                this.simCTBoolean.setSelected(selectSeries.getSimCT());
                this.simRTBoolean.setSelected(selectSeries.getSimRT());
                this.caseTF.setText(selectSeries.getSapNumber());
                this.firstHTDate.setValue(selectSeries.getTherapyDate());
                this.inDay.setValue(selectSeries.getInDay());
                this.outDay.setValue(selectSeries.getOutDay());
                this.compilikationTA.setText(selectSeries.getComplication());
                this.commentTA.setText(selectSeries.getComments());
            }
        }
    }
    
    @FXML
    public void deletePatient(ActionEvent event) {
        TreeItem selectedItem = (TreeItem) patientTable.getSelectionModel().getSelectedItem();
        Patient selectPatient = (Patient) selectedItem.getValue();
        connection.deletePatient(selectPatient);
        updateList();
    }
    
    @FXML
    public void changePatient(ActionEvent event) {
        TreeItem selectedItem = (TreeItem) patientTable.getSelectionModel().getSelectedItem();
        if (selectedItem.getValue() instanceof Patient) {
            this.oldPatient = (Patient) selectedItem.getValue();
        } else {
            this.oldPatient = (Patient) selectedItem.getParent().getValue();
        }
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
        studyTF.setDisable(!status);
        
        birthdayTF.setDisable(!status);
        deathTF.setDisable(!status);
        
        ariaIDTF.setDisable(!status);
        firstNameTF.setDisable(!status);
        
        sizeTF.setDisable(!status);
        lastNameTF.setDisable(!status);
        
        sexBox.setDisable(!status);
        
        tumorTF.setDisable(!status);
        histoTF.setDisable(!status);
        icdoTF.setDisable(!status);
        
        gradTF.setDisable(!status);
        lokalTF.setDisable(!status);
    }
    
    private void changeStatusSessionPanel(boolean status) {
        inDay.setDisable(!status);
        outDay.setDisable(!status);
        caseTF.setDisable(!status);
        firstHTDate.setDisable(!status);
        commentTA.setDisable(!status);
        compilikationTA.setDisable(!status);
        simCTBoolean.setDisable(!status);
        simRTBoolean.setDisable(!status);
    }
    
    private void clearMask() {
        birthdayTF.setValue(null);
        deathTF.setValue(null);
        firstHTDate.setValue(null);
        inDay.setValue(null);
        outDay.setValue(null);
        caseTF.clear();
        studyNameTF.clear();
        commentTA.clear();
        compilikationTA.clear();
        ariaIDTF.clear();
        firstNameTF.clear();
        sizeTF.clear();
        lastNameTF.clear();
        sexBox.setValue("Unbestimmt");
        studyTF.setValue("nein");
        tumorTF.getSelectionModel().clearSelection();
        histoTF.getSelectionModel().clearSelection();
        icdoTF.getSelectionModel().clearSelection();
        gradTF.getSelectionModel().clearSelection();
        lokalTF.getSelectionModel().clearSelection();
        saveNewPatient.setDisable(true);
    }
    
    @FXML
    public void showStudyName(ActionEvent evt) {
        if (studyTF.getValue() != null && !studyTF.getValue().equals("nein")) {
            studyLabel.setVisible(true);
            studyNameTF.setVisible(true);
            studyNameTF.setDisable(false);
        } else {
            studyLabel.setVisible(false);
            studyNameTF.setVisible(false);
            studyNameTF.setDisable(true);
        }
    }
    
    public DatabaseConnection getConnection() {
        return connection;
    }
    
    public void setConnection(DatabaseConnection connection) {
        this.connection = connection;
        updateList();
    }
    
    public void updateList() {
        if (connection != null) {
            updateList(connection.getPatientList());
        }
    }
    
    public void updateList(List<Patient> patients) {
        patientTable.getRoot().getChildren().clear();
        patients.stream().forEach((Patient patient) -> {
            TreeItem treeItem = new TreeItem<>(patient, new ImageView(nodeImage));
            patientTable.getRoot().getChildren().add(treeItem);
            if (patient != null && patient.getSeries() != null && !patient.getSeries().isEmpty()) {
                patient.getSeries().stream().forEach((serie) -> {
                    treeItem.getChildren().add(new TreeItem<>(serie));
                });
                treeItem.setExpanded(true);
            }
        });
        FXCollections.sort(patientTable.getRoot().getChildren(), (TreeItem o1, TreeItem o2) -> {
            Patient p1 = (Patient) o1.getValue();
            Patient p2 = (Patient) o2.getValue();
            return p1.getLastName().compareTo(p2.getLastName());
        });
        
        filterSummary.setText(patients.size() + " von " + connection.getPatientList().size() + " Patienten angezeigt");
    }
    
    public void filterList(ActionEvent ev) {
        List<Patient> temp = connection.getPatientList();
        List<Patient> result = temp.stream().filter(new Predicate<Patient>() {
            @Override
            public boolean test(Patient s) {
                return s.getFirstName().toLowerCase().startsWith(filterFirstName.getText().toLowerCase())
                        && s.getLastName().toLowerCase().startsWith(filterLastName.getText().toLowerCase())
                        && s.getAriaID().toLowerCase().startsWith(filterPatientNumber.getText().toLowerCase());
            }
        }).collect(Collectors.toList());
        updateList(result);
    }
    
    private boolean patientDouble(List<Patient> patientList, String ariaID) {
        boolean found = false;
        for (Patient patientItem : patientList) {
            if (patientItem.getAriaID().equals(ariaID)) {
                found = true;
            }
        }
        return found;
        
    }
    
    private void changeCSS(Node object, boolean addCss, String... cssElement) {
        if (addCss) {
            object.getStyleClass().addAll(cssElement);
        } else {
            object.getStyleClass().removeAll(cssElement);
        }
    }
}
