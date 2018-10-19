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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private String user = "sa";
    private String password = "";
    private Connection con;
    private Statement stmt = null;
    private int result = 0;
    private ICDCode icd10;
    private ICDCode icd3;
    private ICDCode mCode;

    private static final String TN_PATIENT = "patientTable";
    private static final String TN_DIAGNOSIC = "diagnosicTable";
    private static final String TN_STAGING = "stagingTable";
    private static final String TN_SESSION = "sessionTable";

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
            File path = new File(new File(userDir + "\\data\\data.db").getParent());
            if (!path.exists()) {
                path.mkdir();
            }
            Properties p = new Properties();
            p.setProperty("sa", "");
            p.setProperty("useUnicode", "true");
            this.filePath = "jdbc:hsqldb:file:" + path.getAbsolutePath() + "\\data.db";
            //Connect the Database
            con = DriverManager.getConnection(this.filePath + ";hsqldb.lock_file=false", p);
            //con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/Patients",user,password);
            System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Databasepath: " + this.filePath);
            if (con != null) {
                System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Connection created successfully");
                DatabaseMetaData databaseMetaData = con.getMetaData();
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
                stmt = con.createStatement();
                stmt.execute("SET DATABASE TRANSACTION CONTROL mvcc;");
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
                            + "PRIMARY KEY (caseNumber),"
                            + "FOREIGN KEY(ARIAID) REFERENCES diagnosictable (ARIAID) "
                            + "ON UPDATE CASCADE "
                            + "ON DELETE CASCADE);");
                }
            } else {
                System.out.println("[e] " + LocalDate.now() + " " + LocalTime.now() + " Problem with creating connection");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<Patient> getPatientList() {
        ArrayList<Patient> patientList = new ArrayList();
        try {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM PATIENTTABLE;");
            while (resultSet.next()) {
                Patient temp = new Patient();
                temp.setSeries(new ArrayList<>());
                temp.setAriaID(resultSet.getString("ARIAID"));
                temp.setLastName(resultSet.getString("LASTNAME"));
                temp.setFirstName(resultSet.getString("FIRSTNAME"));
                temp.setSex(resultSet.getString("SEX"));
                temp.setBirthday(resultSet.getString("BIRTHDAY") != null ? LocalDate.parse(resultSet.getString("BIRTHDAY")) : null);
                temp.setDeathDay(resultSet.getString("DEATHDAY") != null ? LocalDate.parse(resultSet.getString("DEATHDAY")) : null);
                temp.setStudy(resultSet.getString("STUDY"));
                temp.setPretherapy(resultSet.getBoolean("PRETHERAPY"));
                ResultSet resultDiagnosic = stmt.executeQuery("SELECT * FROM DIAGNOSICTABLE WHERE ARIAID='" + temp.getAriaID() + "';");
                ResultSet resultStaging = stmt.executeQuery("SELECT * FROM STAGINGTABLE WHERE ARIAID='" + temp.getAriaID() + "';");
                ResultSet resultSession = stmt.executeQuery("SELECT * FROM SessionTABLE WHERE ARIAID='" + temp.getAriaID() + "';");
                while (resultDiagnosic.next()) {
                    temp.setDiagnoses(new Diagnosis());
                    temp.getDiagnose().setICD10(icd10.getItem(resultDiagnosic.getString("ICD10")));
                    temp.getDiagnose().setPreop(resultDiagnosic.getBoolean("PREOP"));
                    temp.getDiagnose().setPostop(resultDiagnosic.getBoolean("POSTOP"));
                    temp.getDiagnose().setPrimary(resultDiagnosic.getBoolean("PRIMARYTUMOR"));
                    temp.getDiagnose().setRezidiv(resultDiagnosic.getBoolean("REZIDIV"));
                }
                while (resultStaging.next()) {
                    temp.getDiagnose().setStaging((new Staging()));
                    temp.getDiagnose().getStaging().setmCode(resultStaging.getString("mCode") != null ? mCode.getItem(resultStaging.getString("mCode")) : null);
                    temp.getDiagnose().getStaging().setGrading(resultStaging.getString("GRAD"));
                    temp.getDiagnose().getStaging().setSize(resultStaging.getString("SIZE"));
                    temp.getDiagnose().getStaging().setLokal(resultStaging.getString("LOKAL"));
                }
                while (resultSession.next()) {
                    Series serie = new Series();
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
                patientList.add(temp);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return patientList;
    }

    public void updatePatient(Patient patient, String oldAriaID) {
        try {
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

    public void sqlInsertSession(String ariaID, Series session) {
        try {
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

            stmt.executeUpdate(sql.toString());

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeDB() {
        try {
            System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Connection is closing");
            this.con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean deletePatient(Patient patient) {
        try {
            return stmt.executeUpdate("DELETE FROM PATIENTTABLE WHERE ARIAID='" + patient.getAriaID() + "'") > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Connection getCon() {
        return con;
    }

    public Statement getStmt() {
        return stmt;
    }

}
