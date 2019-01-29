/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb;

import ICD.ICDCode;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import patientdb.data.Diagnosis;
import patientdb.data.Patient;
import patientdb.data.Series;
import patientdb.data.Staging;

/**
 *
 * @author shaesler
 */
public class DatabaseConnection {

    private boolean firstRun = true;
    private String filePath = new String();
    private int result = 0;
    private ICDCode icd10;
    private ICDCode icd3;
    private ICDCode mCode;

    private static final String TN_PATIENT = "patientTable";
    private static final String TN_DIAGNOSIC = "diagnosicTable";
    private static final String TN_STAGING = "stagingTable";
    private static final String TN_SESSION = "sessionTable";

    String url = "jdbc:sqlite:H:\\NetBeansProjects\\EpidApp_Git\\PatientDB\\PatientDB\\data\\data_sqllite.db";

    private final String userDir = System.getProperty("user.dir");

    public DatabaseConnection(ICDCode icd10, ICDCode icd3, ICDCode mCode) {
        this.icd10 = icd10;
        this.icd3 = icd3;
        this.mCode = mCode;
        boolean patientTableExist = false;
        boolean diagnosicTableExist = false;
        boolean stagingTableExist = false;
        boolean sessionTableExist = false;
        
        
        try {
            //create Data-Directory
            File path = new File(new File(userDir + "\\data\\data_sqllite.db").getParent());
            System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Datapath: " + path.getAbsolutePath());
            if (!path.exists()) {
                path.mkdir();
            }
            this.url = "jdbc:sqlite:" + path.getAbsolutePath() + "\\data_sqllite.db";

            try (Connection conn = this.connect()) {
                Statement stmt = conn.createStatement();
                System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Databasepath: " + this.url);
                System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Connection created successfully");
                DatabaseMetaData databaseMetaData = conn.getMetaData();
                //Check if table exists, when no the create the table
                try (ResultSet resultSet = databaseMetaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (resultSet.next()) {
                        String test = resultSet.getString("TABLE_NAME");
                        if (!patientTableExist) {
                            patientTableExist = test.equalsIgnoreCase("patientTable");
                        }
                        if (!diagnosicTableExist) {
                            diagnosicTableExist = test.equalsIgnoreCase("diagnosicTable");
                        }
                        if (!stagingTableExist) {
                            stagingTableExist = test.equalsIgnoreCase("stagingTable");
                        }
                        if (!sessionTableExist) {
                            sessionTableExist = test.equalsIgnoreCase("sessionTable");
                        }
                    }
                }
                if (!patientTableExist) {
                    result = stmt.executeUpdate(
                            "CREATE TABLE " + TN_PATIENT + " (ARIAID VARCHAR(50) NOT NULL,"
                            + "LASTNAME VARCHAR(75) NOT NULL,"
                            + "FIRSTNAME VARCHAR(75) NOT NULL,"
                            + "BIRTHDAY DATE,"
                            + "DEATHDAY DATE,"
                            + "STUDY VARCHAR(25),"
                            + "STUDYNAME VARCHAR(75),"
                            + "PRETHERAPY BOOLEAN,"
                            + "SEX VARCHAR(25),"
                            + "CREATEDATE TIMESTAMP,"
                            + "MODIFYDATE TIMESTAMP,"
                            + "PRIMARY KEY (ARIAID));");

                }
                if (!diagnosicTableExist) {
                    result = stmt.executeUpdate(
                            "CREATE TABLE " + TN_DIAGNOSIC + " (ARIAID VARCHAR(50) NOT NULL,"
                            + "ICD10 VARCHAR(7) NOT NULL,"
                            + "primaryTumor BOOLEAN,"
                            + "rezidiv BOOLEAN,"
                            + "preop BOOLEAN,"
                            + "postop BOOLEAN,"
                            + "PRIMARY KEY (ARIAID),"
                            + "FOREIGN KEY(ARIAID) REFERENCES patienttable (ARIAID) "
                            + "ON UPDATE CASCADE "
                            + "ON DELETE CASCADE);");
                }
                if (!stagingTableExist) {
                    result = stmt.executeUpdate(
                            "CREATE TABLE " + TN_STAGING + "(ARIAID VARCHAR(50) NOT NULL,"
                            + "mCode VARCHAR(7),"
                            + "grad VARCHAR(25),"
                            + "size VARCHAR(25),"
                            + "lokal VARCHAR(25),"
                            + "PRIMARY KEY (ARIAID),"
                            + "FOREIGN KEY(ARIAID) REFERENCES diagnosictable (ARIAID) "
                            + "ON UPDATE CASCADE "
                            + "ON DELETE CASCADE);");
                }
                if (!sessionTableExist) {
                    result = stmt.executeUpdate(
                            "CREATE TABLE " + TN_SESSION + " (ARIAID VARCHAR(50) NOT NULL,"
                            + "simChemo boolean,"
                            + "simRT boolean,"
                            + "sessionDate DATE,"
                            + "inDay DATE,"
                            + "outDay DATE,"
                            + "caseNumber VARCHAR(75),"
                            + "comments LONGVARCHAR,"
                            + "problems LONGVARCHAR,"
                            + "uniqueID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,"
                            + "FOREIGN KEY(ARIAID) REFERENCES diagnosictable (ARIAID) "
                            + "ON UPDATE CASCADE "
                            + "ON DELETE CASCADE);");
                }
            }
        } catch (SQLException ex) {
            System.err.println("[e] " + LocalDate.now() + " " + LocalTime.now() + " Problem with creating connection");
            System.exit(-1);
        }

    }

    public ObservableList<String> getSAPNumbers(String ariaID){
        String sql = "SELECT CASENUMBER FROM SESSIONTABLE WHERE ARIAID = ? Group by casenumber;";
        ArrayList<String> sapList = new ArrayList();
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ariaID);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                sapList.add(rs.getString("caseNumber"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return FXCollections.observableList(sapList);
    }
    public List<Patient> getPatientList() {
        String sql = "SELECT * FROM patienttable";
        ArrayList<Patient> patientList = new ArrayList();
        try (Connection conn = this.connect();
                Statement stmtList = conn.createStatement();
                ResultSet rs = stmtList.executeQuery(sql)) {

            while (rs.next()) {
                Patient temp = new Patient();
                temp.setSeries(new ArrayList<>());
                temp.setAriaID(rs.getString("ARIAID"));
                temp.setLastName(rs.getString("LASTNAME"));
                temp.setFirstName(rs.getString("FIRSTNAME"));
                temp.setSex(rs.getString("SEX"));
                temp.setBirthday(rs.getString("BIRTHDAY") != null ? LocalDate.parse(rs.getString("BIRTHDAY")) : null);
                temp.setDeathDay(rs.getString("DEATHDAY") != null ? LocalDate.parse(rs.getString("DEATHDAY")) : null);
                temp.setStudy(rs.getString("STUDY"));
                temp.setPretherapy(rs.getBoolean("PRETHERAPY"));
                try (Statement stmtDiagnosic = conn.createStatement();
                        ResultSet resultDiagnosic = stmtDiagnosic.executeQuery("SELECT * FROM DIAGNOSICTABLE WHERE ARIAID='" + temp.getAriaID() + "';")) {
                    while (resultDiagnosic.next()) {
                        temp.setDiagnoses(new Diagnosis());
                        temp.getDiagnose().setICD10(icd10.getItem(resultDiagnosic.getString("ICD10")));
                        temp.getDiagnose().setPreop(resultDiagnosic.getBoolean("PREOP"));
                        temp.getDiagnose().setPostop(resultDiagnosic.getBoolean("POSTOP"));
                        temp.getDiagnose().setPrimary(resultDiagnosic.getBoolean("PRIMARYTUMOR"));
                        temp.getDiagnose().setRezidiv(resultDiagnosic.getBoolean("REZIDIV"));
                    }
                }
                try (Statement stmtStaging = conn.createStatement();
                        ResultSet resultStaging = stmtStaging.executeQuery("SELECT * FROM STAGINGTABLE WHERE ARIAID='" + temp.getAriaID() + "';")) {
                    while (resultStaging.next()) {
                        temp.getDiagnose().setStaging((new Staging()));
                        temp.getDiagnose().getStaging().setmCode(resultStaging.getString("mCode") != null ? mCode.getItem(resultStaging.getString("mCode")) : null);
                        temp.getDiagnose().getStaging().setGrading(resultStaging.getString("GRAD"));
                        temp.getDiagnose().getStaging().setSize(resultStaging.getString("SIZE"));
                        temp.getDiagnose().getStaging().setLokal(resultStaging.getString("LOKAL"));
                    }
                }
                try (Statement stmtSession = conn.createStatement();
                        ResultSet resultSession = stmtSession.executeQuery("SELECT * FROM SessionTABLE WHERE ARIAID='" + temp.getAriaID() + "';")) {
                    while (resultSession.next()) {
                        Series serie = new Series(resultSession.getInt("uniqueID"));
                        serie.setSimCT(resultSession.getBoolean("SIMCHEMO"));
                        serie.setSimRT(resultSession.getBoolean("simRT"));
                        serie.setTherapyDate(resultSession.getString("sessionDate") != null ? LocalDate.parse(resultSession.getString("sessionDate")) : null);
                        serie.setInDay(resultSession.getString("inDay") != null ? LocalDate.parse(resultSession.getString("inDay")) : null);
                        serie.setOutDay(resultSession.getString("outDay") != null ? LocalDate.parse(resultSession.getString("outDay")) : null);
                        serie.setSapNumber(resultSession.getString("caseNumber"));
                        serie.setComments(resultSession.getString("comments"));
                        serie.setComplication(resultSession.getString("problems"));
                        temp.getSeries().add(serie);
                    }
                }
                patientList.add(temp);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return patientList;
    }

    public void updatePatient(Patient patient, String oldAriaID) {
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement()) {
            if (patient.getAriaID() != null && patient.getFirstName() != null && patient.getLastName() != null) {

                if (rowCount("patientTable", oldAriaID) <= 0) {
                    stmt.executeUpdate(sqlInsertPatient(patient));
                } else {
                    stmt.executeUpdate(sqlUpdatePatient(patient, oldAriaID));
                    oldAriaID = patient.getAriaID();
                }
                if (rowCount("diagnosicTable", oldAriaID) <= 0) {
                    stmt.executeUpdate(sqlInsertDiagnosic(patient));
                } else {
                    stmt.executeUpdate(sqlUpdateDiagnosic(patient, oldAriaID));
                }
                if (rowCount("stagingTable", oldAriaID) <= 0) {
                    stmt.executeUpdate(sqlInsertStaging(patient));
                } else {
                    stmt.executeUpdate(sqlUpdateStaging(patient, oldAriaID));
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Speichern erfolgreich!");
                alert.setHeaderText(null);
                alert.setContentText("Die Daten wurden erfolgreich gespeichert!");

                alert.showAndWait();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler - Datenbankfehler");
            alert.setHeaderText("Datenbankfehler");
            alert.setContentText(ex.getLocalizedMessage());
            alert.showAndWait();
        }
    }

    public int rowCount(String tableName, String whereAriaIDArgument) throws SQLException {
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement()) {
            String sql = "SELECT COUNT(*) FROM " + tableName;
            if (whereAriaIDArgument != null) {
                sql += " WHERE ariaID = '" + whereAriaIDArgument + "';";
            } else {
                sql += ";";
            }
            ResultSet resultSet = stmt.executeQuery(sql);
            int rowCount = -1;
            while (resultSet.next()) {
                rowCount = Integer.parseInt(resultSet.getString(1));
            }
            return rowCount;
        }

    }

    private String sqlInsertPatient(Patient patient) {
        StringBuilder sql = new StringBuilder("INSERT INTO patientTable (");
        sql.append("ARIAID, LASTNAME, FIRSTNAME");
        if (patient.getBirthday() != null) {
            sql.append(",BIRTHDAY");
        }
        if (patient.getDeathDay() != null) {
            sql.append(",DEATHDAY");
        }
        if (patient.getStudy() != null) {
            sql.append(",STUDY");
        }
        if (patient.getPretherapy() != null) {
            sql.append(",PRETHERAPY");
        }
        if (patient.getSex() != null) {
            sql.append(",SEX");
        }
        if (patient.getStudyName() != null) {
            sql.append(",STUDYNAME");
        }
        sql.append(", CREATEDATE, MODIFYDATE) VALUES ('");
        sql.append(patient.getAriaID()).append("','");
        sql.append(patient.getLastName()).append("','");
        sql.append(patient.getFirstName()).append("',");
        if (patient.getBirthday() != null) {
            sql.append("'").append(patient.getBirthday()).append("',");
        }
        if (patient.getDeathDay() != null) {
            sql.append("'").append(patient.getDeathDay()).append("',");
        }
        if (patient.getStudy() != null) {
            sql.append("'").append(patient.getStudy()).append("',");
        }
        if (patient.getPretherapy() != null) {
            sql.append("'").append(patient.getPretherapy()).append("',");
        }
        if (patient.getSex() != null) {
            sql.append("'").append(patient.getSex()).append("',");
        }
        if (patient.getStudyName() != null) {
            sql.append("'").append(patient.getStudyName()).append("',");
        }
        sql.append("CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);");
        return sql.toString();
    }

    private String sqlInsertDiagnosic(Patient patient) {
        StringBuilder sql = new StringBuilder("INSERT INTO diagnosictable (");
        sql.append("ARIAID, ICD10");
        sql.append(" ,PRIMARYTUMOR");
        sql.append(" ,REZIDIV");
        sql.append(" ,POSTOP");
        sql.append(" ,PREOP");
        sql.append(") VALUES ('");
        sql.append(patient.getAriaID()).append("', '");
        if (patient.getDiagnose().getICD10().getCode() != null) {
            sql.append(patient.getDiagnose().getICD10().getCode()).append("', ");
        }
        sql.append(patient.getDiagnose().isPrimary()).append(", ");
        sql.append(patient.getDiagnose().isRezidiv()).append(", ");
        sql.append(patient.getDiagnose().isPostop()).append(", ");
        sql.append(patient.getDiagnose().isPreop()).append(");");

        return sql.toString();
    }

    private String sqlInsertStaging(Patient patient) {
        StringBuilder sql = new StringBuilder("INSERT INTO stagingtable (");
        sql.append("ARIAID");
        if (patient.getDiagnose().getStaging().getmCode() != null) {
            sql.append(" ,MCODE");
        }
        if (patient.getDiagnose().getStaging().getGrading() != null) {
            sql.append(" ,GRAD");
        }
        if (patient.getDiagnose().getStaging().getSize() != null) {
            sql.append(" ,SIZE");
        }
        if (patient.getDiagnose().getStaging().getLokal() != null) {
            sql.append(" ,LOKAL");
        }
        sql.append(") VALUES ('");
        sql.append(patient.getAriaID()).append("'");
        if (patient.getDiagnose().getStaging().getmCode() != null) {
            sql.append(",'").append(patient.getDiagnose().getStaging().getmCode().getCode()).append("'");
        }
        if (patient.getDiagnose().getStaging().getGrading() != null) {
            sql.append(",'").append(patient.getDiagnose().getStaging().getGrading()).append("'");
        }
        if (patient.getDiagnose().getStaging().getSize() != null) {
            sql.append(",'").append(patient.getDiagnose().getStaging().getSize()).append("'");
        }
        if (patient.getDiagnose().getStaging().getLokal() != null) {
            sql.append(",'").append(patient.getDiagnose().getStaging().getLokal()).append("'");
        }
        sql.append(");");

        return sql.toString();
    }

    private String sqlUpdatePatient(Patient patient, String ariaID) {
        StringBuilder sql = new StringBuilder("UPDATE patientTable SET ");
        sql.append("ARIAID ='").append(patient.getAriaID()).append("',");
        sql.append("LASTNAME ='").append(patient.getLastName()).append("',");
        sql.append("FIRSTNAME ='").append(patient.getFirstName()).append("',");
        if (patient.getBirthday() != null) {
            sql.append("BIRTHDAY ='").append(patient.getBirthday()).append("',");
        }
        if (patient.getDeathDay() != null) {
            sql.append("DEATHDAY ='").append(patient.getDeathDay()).append("',");
        }
        if (patient.getStudy() != null) {
            sql.append("STUDY ='").append(patient.getStudy()).append("',");
        }
        if (patient.getPretherapy() != null) {
            sql.append("PRETHERAPY ='").append(patient.getPretherapy()).append("',");
        }
        if (patient.getSex() != null) {
            sql.append("SEX ='").append(patient.getSex()).append("',");
        }
        if (patient.getStudyName() != null) {
            sql.append("SEX ='").append(patient.getStudyName()).append("',");
        }
        sql.append("MODIFYDATE =CURRENT_TIMESTAMP WHERE ARIAID ='").append(ariaID).append("';");
        System.out.println(sql);
        return sql.toString();
    }

    private String sqlUpdateDiagnosic(Patient patient, String ariaID) {
        StringBuilder sql = new StringBuilder("UPDATE diagnosicTable SET ");
        sql.append("ARIAID ='").append(patient.getAriaID()).append("',");
        sql.append("ICD10 ='").append(patient.getDiagnose().getICD10().getCode()).append("'");
        sql.append(",").append("PRIMARYTUMOR =").append(patient.getDiagnose().isPrimary());
        sql.append(",").append("REZIDIV =").append(patient.getDiagnose().isRezidiv());
        sql.append(",").append("PREOP =").append(patient.getDiagnose().isPreop());
        sql.append(",").append("POSTOP =").append(patient.getDiagnose().isPostop());
        sql.append(" WHERE ARIAID ='").append(ariaID).append("';");

        return sql.toString();
    }

    private String sqlUpdateStaging(Patient patient, String ariaID) {
        StringBuilder sql = new StringBuilder("UPDATE stagingTable SET ");
        sql.append("ARIAID ='").append(patient.getAriaID()).append("'");
        if (patient.getDiagnose().getStaging().getmCode() != null) {
            sql.append(",").append("mcode ='").append(patient.getDiagnose().getStaging().getmCode().getCode()).append("'");
        }
        if (patient.getDiagnose().getStaging().getGrading() != null) {
            sql.append(",").append("grad ='").append(patient.getDiagnose().getStaging().getGrading()).append("'");
        }
        if (patient.getDiagnose().getStaging().getSize() != null) {
            sql.append(",").append("size ='").append(patient.getDiagnose().getStaging().getSize()).append("'");
        }
        if (patient.getDiagnose().getStaging().getLokal() != null) {
            sql.append(",").append("lokal ='").append(patient.getDiagnose().getStaging().getLokal()).append("'");
        }
        sql.append(" WHERE ARIAID ='").append(ariaID).append("';");

        return sql.toString();
    }

    public boolean sqlUpdateSession(String ariaID, long uniqueID, Series session) {
        StringBuilder sql = new StringBuilder("UPDATE sessionTable SET ");
        sql.append("ARIAID ='").append(ariaID).append("'");
        if (session.getTherapyDate() != null) {
            sql.append(",").append("sessionDate ='").append(session.getTherapyDate()).append("'");
        }
        if (session.getInDay() != null) {
            sql.append(",").append("inDay ='").append(session.getInDay()).append("'");
        }
        if (session.getOutDay() != null) {
            sql.append(",").append("outDay ='").append(session.getOutDay()).append("'");
        }
        if (session.getSapNumber() != null) {
            sql.append(",").append("CaseNumber ='").append(session.getSapNumber()).append("'");
        }
        if (session.getComments() != null) {
            sql.append(",").append("comments ='").append(session.getComments()).append("'");
        }
        if (session.getComplication() != null) {
            sql.append(",").append("problems ='").append(session.getComplication()).append("'");
        }
        sql.append(",").append("simChemo =").append(session.getSimCT()).append(" ");
        sql.append(",").append("simRT =").append(session.getSimRT()).append(" ");
        sql.append(" WHERE uniqueID ='").append(uniqueID).append("';");
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql.toString()) > 0;

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(sql);
        }
        return false;
    }

    public boolean sqlInsertSession(String ariaID, Series session) {

        StringBuilder sql = new StringBuilder("INSERT INTO sessiontable (");
        sql.append("ARIAID, simChemo, simRT");
        if (session.getTherapyDate() != null) {
            sql.append(" ,sessionDate");
        }
        if (session.getInDay() != null) {
            sql.append(" ,inDay");
        }
        if (session.getOutDay() != null) {
            sql.append(" ,outDay");
        }
        if (session.getSapNumber() != null) {
            sql.append(" ,CaseNumber");
        }
        if (session.getComments() != null) {
            sql.append(" ,comments");
        }
        if (session.getComplication() != null) {
            sql.append(" ,problems");
        }
        sql.append(") VALUES ('");
        sql.append(ariaID).append("'");
        sql.append(",").append(session.getSimCT());
        sql.append(",").append(session.getSimRT());
        if (session.getTherapyDate() != null) {
            sql.append(",'").append(session.getTherapyDate()).append("'");
        }
        if (session.getInDay() != null) {
            sql.append(",'").append(session.getInDay()).append("'");
        }
        if (session.getOutDay() != null) {
            sql.append(",'").append(session.getOutDay()).append("'");
        }
        if (session.getSapNumber() != null) {
            sql.append(",'").append(session.getSapNumber()).append("'");
        }
        if (session.getComments() != null) {
            sql.append(",'").append(session.getComments()).append("'");
        }
        if (session.getComplication() != null) {
            sql.append(",'").append(session.getComplication()).append("'");
        }
        sql.append(");");
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql.toString()) > 0;

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deletePatient(Patient patient) {
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate("DELETE FROM PATIENTTABLE WHERE ARIAID='" + patient.getAriaID() + "'") > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean deleteSession(Series serie) {
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate("DELETE FROM SESSIONTABLE WHERE UNIQUEID='" + serie.getUniqueID() + "'") > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
